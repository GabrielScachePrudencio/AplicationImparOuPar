# 🧠🎮 Jogo Ímpar ou Par - Java com Swing (Multiplayer via Rede Local)

Este é um jogo de **Ímpar ou Par multiplayer**, desenvolvido em **Java**, com interface gráfica usando **Swing** e comunicação de rede via **Sockets TCP/IP** para partidas em rede local (LAN).  

Um jogador atua como **servidor**, e outro como **cliente**, conectando-se via IP configurado em um arquivo XML.

---

## 📦 Estrutura do Projeto

AplicationImparOuPar/
├── src/
│ └── main/
│ └── java/
│ └── ImparOuPar/
│ ├── network/ # Código de comunicação (servidor e cliente)
│ ├── xml/ # Configuração do IP no config.xml
│ └── ... # Outras classes do jogo


---

## 🚀 Como Rodar o Projeto

### 1. Clonar o repositório

2. executar esse codigo: git clone https://github.com/GabrielScachePrudencio/AplicationImparOuPar.git
cd AplicationImparOuPar

3. edite seu config.xml de acordo com seu ip na pasta: AplicationImparOuPar/src/main/java/ImparOuPar/xml/config.xml

4. execute o Servidor.java no AplicationImparOuPar/src/main/java/ImparOuPar/network/Servidor.java
 
5. execute o Cliente.java no AplicationImparOuPar/src/main/java/ImparOuPar/network/Cliente.java 
