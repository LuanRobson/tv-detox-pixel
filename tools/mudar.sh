#!/usr/bin/env bash
# Faz UMA mudança na TV, anota no log com o comando que desfaz e regenera desfazer-tudo.sh.
#
#   tools/mudar.sh desligar <pacote> "<motivo>"
#   tools/mudar.sh setting  <global|system|secure> <chave> <valor> "<motivo>"
#
# Log: mudancas.log (uma linha por mudança, separada por TAB):
#   data  tipo  alvo  valor_antigo  valor_novo  comando  desfazer  motivo
set -u
cd "$(dirname "$0")/.."  # log e desfazer-tudo.sh ficam na raiz do repo
DEV=${DEV:?"defina o IP da TV: DEV=192.168.x.x:5555"}
LOG=mudancas.log
UNDO=desfazer-tudo.sh
tv() { adb -s "$DEV" shell "$@" | tr -d '\r'; }

[ -f "$LOG" ] || printf 'data\ttipo\talvo\tantes\tdepois\tcomando\tdesfazer\tmotivo\n' > "$LOG"

regen_undo() {
  {
    echo '#!/usr/bin/env bash'
    echo '# Gerado por tools/mudar.sh a partir de mudancas.log. Desfaz tudo, da última mudança para a primeira.'
    echo "# Uso: ./desfazer-tudo.sh   (TV em ${DEV}; troque com DEV=ip:porta ./desfazer-tudo.sh)"
    echo 'DEV=${DEV:-'"$DEV"'}'
    echo 'adb connect "$DEV" >/dev/null 2>&1 || true'
    echo 'run() { echo "+ $*"; adb -s "$DEV" shell "$@"; }'
    tail -n +2 "$LOG" | awk -F'\t' '{print $7}' | sed '1!G;h;$!d' | grep -v '^$' | sed 's/^adb shell /run /'
    echo 'echo "Pronto. Reinicie a TV (adb -s $DEV reboot) para garantir que tudo voltou."'
  } > "$UNDO"
  chmod +x "$UNDO"
}

log() { # tipo alvo antes depois comando desfazer motivo
  printf '%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$@" >> "$LOG"
  regen_undo
}

case "${1:-}" in
  desligar)
    p=$2; why=${3:-}
    if tv pm list packages -d "$p" | grep -qx "package:$p"; then echo "já desligado: $p"; exit 0; fi
    tv pm list packages "$p" | grep -qx "package:$p" || { echo "não instalado: $p" >&2; exit 1; }
    out=$(tv pm disable-user --user 0 "$p" 2>&1)
    if tv pm list packages -d "$p" | grep -qx "package:$p"; then
      log app "$p" enabled disabled-user "adb shell pm disable-user --user 0 $p" "adb shell pm enable $p" "$why"
      echo "desligado: $p"
    else
      echo "FALHOU: $p -> $out" >&2; exit 1
    fi ;;
  setting)
    ns=$2; key=$3; val=$4; why=${5:-}
    old=$(tv settings get "$ns" "$key")
    tv settings put "$ns" "$key" "$val"
    new=$(tv settings get "$ns" "$key")
    [ "$new" = "$val" ] || { echo "FALHOU: $ns $key ficou '$new'" >&2; exit 1; }
    if [ "$old" = "null" ]; then undo="adb shell settings delete $ns $key"; else undo="adb shell settings put $ns $key $old"; fi
    log setting "$ns/$key" "$old" "$val" "adb shell settings put $ns $key $val" "$undo" "$why"
    echo "$ns $key: $old -> $val" ;;
  registrar) # mudança feita por outro caminho (ex.: instalar APK): tipo alvo antes depois comando desfazer motivo
    shift; log "$@"; echo "registrado: $2" ;;
  *) sed -n '2,8p' "$0"; exit 1 ;;
esac
