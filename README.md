# Plugin para Teste do Processo Seletivo da Enxada Host

Este plugin foi desenvolvido como parte do processo seletivo da Enxada Host. Abaixo, você encontrará detalhes sobre os comandos disponíveis e uma explicação detalhada de cada função configurável no arquivo `config.yml`.

## Detalhes dos Comandos

### ListHomeCommand (/homelist):
- Lista as homes do usuário, baseado no MySQL, usando o UUID do jogador como referência.

### HomeCommand (/home <nome_da_home>):
- Teleporta o jogador para a home especificada (para buscar o nome da home, use o comando `/homelist`).

### SetHomeCommand (/sethome <nome_da_home>):
- Define uma home para o jogador com o nome especificado.

### RemoveHomeCommand (/homeremove <nome_da_home>):
- Remove a home definida pelo jogador.

### ReloadCommand (/reloadconfig):
- Recarrega a configuração do plugin (config.yml) para atualizar as mudanças feitas.

## Configurações do Plugin

### WIND_CHARGE:
- **`explosionForce`**: Define o raio da explosão. Quanto maior o valor, maior a destruição causada pelo projétil de vento (wind charge).
- **`addParticles`**: Define se o projétil de vento terá partículas de rastro ou não.
- **`projectileSpeed`**: Ajusta a velocidade do projétil de vento.
- **`projectileParticle`**: Define qual partícula seguirá o projétil. Veja mais opções de partículas [aqui](https://minecraft.fandom.com/wiki/Particles).

### HOME:
- **`cooldown`**: Define o tempo de espera, em segundos, antes de poder usar o comando de home novamente.
- **`teleport-particles`**: Define se haverá partículas exibidas ao teletransportar-se para a home.
- **`particle-type`**: Define o comportamento das partículas. Opções disponíveis:
    - `CIRCLE`: Um círculo abaixo do jogador.
    - `SPIRAL`: Um espiral tridimensional em volta do jogador.
- **`particle-name`**: Define a partícula que aparecerá para o jogador ao se teletransportar para a home. Veja mais opções de partículas [aqui](https://minecraft.fandom.com/wiki/Particles).

### Uso do MySQL:
- **`host`**: Define o IP do banco de dados. Para um banco de dados local, use 'localhost' ou '127.0.0.1'.
- **`port`**: A porta padrão do MySQL é 3306.
- **`database`**: O nome do banco de dados que você deseja usar.
- **`username`**: O usuário do banco de dados, geralmente aquele que você usa para fazer login.
- **`password`**: A senha para acessar o banco de dados. Caso não tenha senha, use aspas duplas vazias (`""`).

---

Certifique-se de configurar o arquivo `config.yml` corretamente para que todas as funcionalidades do plugin funcionem como esperado.
