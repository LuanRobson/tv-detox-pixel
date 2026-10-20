#!/usr/bin/env bash
# Backup do estado da TV (somente leitura). Uso: tools/backup.sh [sufixo]
set -u
DEV=${DEV:?"defina o IP da TV: DEV=192.168.x.x:5555"}
A=(adb -s "$DEV")
TS=$(date +%Y%m%d-%H%M%S)${1:+-$1}
B="$(dirname "$0")/../backup/$TS"
mkdir -p "$B"
"${A[@]}" shell pm list packages -f -u > "$B/pacotes-todos.txt"
"${A[@]}" shell pm list packages -d    > "$B/pacotes-desativados.txt"
"${A[@]}" shell pm list packages -e    > "$B/pacotes-ativos.txt"
"${A[@]}" shell pm list packages -3    > "$B/pacotes-terceiros.txt"
"${A[@]}" shell pm list packages -s    > "$B/pacotes-sistema.txt"
for ns in global system secure; do
  "${A[@]}" shell settings list $ns > "$B/settings-$ns.txt"
done
"${A[@]}" shell cat /proc/meminfo > "$B/meminfo.txt"
"${A[@]}" shell dumpsys meminfo   > "$B/dumpsys-meminfo.txt"
"${A[@]}" shell getprop           > "$B/getprop.txt"
"${A[@]}" shell cmd package query-activities -a android.intent.action.MAIN -c android.intent.category.HOME > "$B/homes.txt" 2>&1
"${A[@]}" shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME > "$B/home-atual.txt" 2>&1
echo "$B"
