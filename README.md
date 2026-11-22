# Painel Nexus — Android Debug

Projeto Android em Kotlin, pronto para compilar como APK Debug pelo GitHub Actions.

## O que está implementado
- Identidade visual preta com vermelho neon.
- Tela inicial com autorização de consentimento `VpnService`.
- Solicitação separada da permissão de sobreposição Android.
- Botão para abrir Free Fire (pacote padrão) ou Free Fire MAX e exibir um painel flutuante.
- Painel arrastável com menu recolhível e controles visuais demonstrativos.
- Workflow do GitHub Actions para gerar e publicar o APK como Artifact.

## Limite funcional importante
A tela solicita apenas o consentimento de VPN do Android. Este projeto **não estabelece um túnel VPN**, não captura tráfego do Free Fire, não lê memória de processos e não obtém coordenadas de jogadores. Os itens ESP são controles visuais sem ligação com o jogo. Uma VPN local não concede acesso à memória privada de outro aplicativo.

## Compilar
- Android Gradle Plugin 8.7.3
- Gradle 8.9
- JDK 17
- Android SDK 35

No GitHub: envie o projeto para a branch `main`, abra **Actions**, execute `Build Painel Nexus Debug APK` e baixe o artifact `Painel-Nexus-Debug`.

APK esperado: `app/build/outputs/apk/debug/app-debug.apk` (o nome pode incluir o sufixo `.debug` conforme a configuração Gradle).
