# Imagens e fontes da home

O que estiver nesta pasta vai para dentro do app quando você roda `sh build.sh`.

| Arquivo | Para quê | Obrigatório? |
|---|---|---|
| `fonts/` | As três fontes pixel (Jersey 10, Pixelify Sans, Silkscreen). Licença em `fonts/OFL-licencas.txt`. | Já vem no repo |
| `wallpaper.gif` | Fundo animado. Toca só enquanto a home está na tela. | Não |
| `wallpaper.jpg` | Fundo parado, usado se não houver `wallpaper.gif`. | Não |
| `avatar.jpg` | Sua foto, num quadrado pixelado ao lado do Wi-Fi. | Não |

Sem `wallpaper.*`, o fundo é um degradê escuro. Sem `avatar.jpg`, o quadradinho some.

As suas imagens **não vão para o GitHub** (estão no `.gitignore`), porque costumam ser arte de outras pessoas. Elas ficam só no seu computador e no seu APK.

Dicas:
- **Fundo:** use 16:9 ou próximo disso (ex.: 1920×1080, ou 1440×720 para pixel art). GIF muito grande pesa na memória da TV: até ~3 MB vai bem numa TV com 1,5–2 GB.
- **Avatar:** basta uma imagem quadrada de uns 150–200 px.
