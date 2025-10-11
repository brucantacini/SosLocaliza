# 🚀 Guia Completo - Executar SOS Localiza no IntelliJ IDEA

## 📋 Pré-requisitos

### Software Necessário
- **IntelliJ IDEA** (Community ou Ultimate)
- **Java 21** (JDK 21)
- **Maven** (incluído no IntelliJ)
- **Git** (para clonar o repositório)

### Conta Twilio (Opcional)
- Account SID
- Auth Token
- Número Trial

---

## 🔧 Configuração Inicial

### 1. Clonar o Repositório
```bash
git clone https://github.com/brucantacini/SosLocaliza.git
```

### 2. Abrir no IntelliJ IDEA
1. **File** → **Open**
2. Selecione a pasta `SosLocaliza`
3. Aguarde o IntelliJ indexar o projeto
4. Confirme se o Maven importou as dependências

### 3. Configurar JDK
1. **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. **Project** → **Project SDK**: Selecione JDK 21
3. **Project** → **Language Level**: 21
4. **Modules** → Selecione o módulo → **Language Level**: 21

---

## ⚙️ Configuração das Variáveis de Ambiente

### Opção 1: Configuração no IntelliJ (Recomendado)
1. **Run** → **Edit Configurations**
2. Clique no **+** → **Spring Boot**
3. Configure:
   - **Name**: `SosLocaliza Application`
   - **Main class**: `com.example.SosLocaliza.SosLocalizaApplication`
   - **Environment variables**:
     ```
     TWILIO_ACCOUNT_SID=ACb84a1c0afbb6628678209002a9db4d6c
     TWILIO_AUTH_TOKEN=7a420445aa2fc29161ac7cc22e2b8353
     TWILIO_TRIAL_NUMBER=+5511989302572
     ```
4. **Apply** → **OK**

### Opção 2: Arquivo .env (Alternativo)
1. Crie um arquivo `.env` na raiz do projeto:
```env
TWILIO_ACCOUNT_SID=ACb84a1c0afbb6628678209002a9db4d6c
TWILIO_AUTH_TOKEN=7a420445aa2fc29161ac7cc22e2b8353
TWILIO_TRIAL_NUMBER=+5511989302572
```

---

## 🚀 Executando a Aplicação

### Método 1: Botão Run (Mais Fácil)
1. Abra o arquivo `SosLocalizaApplication.java`
2. Clique no botão **▶️** ao lado da classe
3. Ou use **Shift+F10**

### Método 2: Run Configuration
1. **Run** → **Run 'SosLocaliza Application'**
2. Ou use **Shift+F10**

### Método 3: Terminal do IntelliJ
1. Abra o terminal integrado (**Alt+F12**)
2. Execute:
```bash
./mvnw spring-boot:run
```

---

## 🧪 Testando a Aplicação

### 1. Verificar se a Aplicação Subiu
- **URL**: http://localhost:8080
- **Status**: Deve retornar 404 (normal, não há endpoint raiz)

### 2. Acessar o Swagger UI
- **URL**: http://localhost:8080/swagger-ui.html
- **Funcionalidade**: Interface interativa para testar a API

### 3. Testar Endpoints via Swagger

#### Criar um Evento
1. Acesse http://localhost:8080/swagger-ui.html
2. Clique em **Eventos**
3. Expanda **POST /eventos/add**
4. Clique em **Try it out**
5. Cole o JSON:
```json
{
  "nomeEvento": "Inundação Teste",
  "descricaoEvento": "Teste de evento via Swagger",
  "causas": "Chuva intensa",
  "alertas": "Área de risco",
  "acoesAntes": "Evacuar área",
  "acoesDurante": "Buscar abrigo",
  "acoesDepois": "Verificar danos"
}
```
6. Clique em **Execute**
7. **Resultado esperado**: Status 201 com dados do evento criado

#### Listar Eventos
1. Expanda **GET /eventos/getAll**
2. Clique em **Try it out** → **Execute**
3. **Resultado esperado**: Status 200 com lista de eventos

#### Enviar SMS
1. Clique em **Sms**
2. Expanda **POST /sms**
3. Clique em **Try it out**
4. Cole o JSON:
```json
{
  "remetente": "SOS Localiza",
  "ddd": "11",
  "numero": "999999999",
  "mensagem": "Teste de SMS via Swagger",
  "idEvento": "ID_DO_EVENTO_CRIADO_ANTERIORMENTE"
}
```
5. Clique em **Execute**
6. **Resultado esperado**: Status 201 (sucesso) ou 503 (Twilio indisponível)

---

## 🔍 Testando com Postman/Insomnia

### 1. Importar Coleção
1. Abra Postman ou Insomnia
2. **Import** → **File**
3. Selecione `postman-collection.json`
4. Configure a variável `base_url`: `http://localhost:8080`

### 2. Executar Testes
1. **Criar Evento** → Anote o `idEvento`
2. **Listar Eventos** → Verifique a resposta
3. **Enviar SMS** → Use o ID anotado
4. **Listar SMS** → Verifique o histórico

---

## 🗄️ Acessando o Banco H2

### Console H2 (Desenvolvimento)
1. **URL**: http://localhost:8080/h2-console
2. **JDBC URL**: `jdbc:h2:mem:testdb`
3. **Username**: `sa`
4. **Password**: `password`
5. **Connect**

### Consultas Úteis
```sql
-- Listar eventos
SELECT * FROM EVENTO;

-- Listar SMS
SELECT * FROM SMS_MESSAGE;

-- Contar registros
SELECT COUNT(*) FROM EVENTO;
SELECT COUNT(*) FROM SMS_MESSAGE;
```

---

## 🐛 Troubleshooting

### Erro: "Port 8080 already in use"
```bash
# Encontrar processo usando a porta
netstat -ano | findstr :8080

# Matar processo (substitua PID)
taskkill /PID <PID> /F
```

### Erro: "Java version not supported"
1. **File** → **Project Structure**
2. **Project** → **Project SDK**: JDK 21
3. **Modules** → **Language Level**: 21

### Erro: "Maven dependencies not found"
1. **View** → **Tool Windows** → **Maven**
2. Clique no ícone de refresh (🔄)
3. Ou **File** → **Reload Maven Projects**

### Erro: "Twilio credentials not found"
1. Verifique se as variáveis de ambiente estão configuradas
2. **Run** → **Edit Configurations**
3. Adicione as variáveis na seção **Environment variables**

### Erro: "Application failed to start"
1. Verifique os logs no console do IntelliJ
2. Confirme se o banco H2 está funcionando
3. Verifique se não há conflitos de porta

---

## 📊 Verificando se Está Funcionando

### Checklist de Validação
- [ ] Aplicação inicia sem erros
- [ ] Swagger UI acessível em http://localhost:8080/swagger-ui.html
- [ ] Console H2 acessível em http://localhost:8080/h2-console
- [ ] Endpoint de eventos funciona (POST/GET)
- [ ] Endpoint de SMS funciona (POST)
- [ ] Validações funcionam (campos obrigatórios)
- [ ] Tratamento de erros funciona (404, 400, 500)

### Logs Esperados
```
Started SosLocalizaApplication in X.XXX seconds
H2 Console available at '/h2-console'
Swagger UI available at '/swagger-ui.html'
```

---

## 🎯 Próximos Passos

### Para Desenvolvimento
1. Explore o código fonte
2. Adicione novos endpoints
3. Implemente testes unitários
4. Configure banco Oracle para produção

### Para Apresentação
1. Demonstre o Swagger UI
2. Mostre os endpoints funcionando
3. Explique a arquitetura
4. Apresente o repositório GitHub

---

## 📞 Suporte

### Problemas Comuns
- **Aplicação não inicia**: Verifique JDK 21 e variáveis de ambiente
- **Swagger não carrega**: Aguarde a aplicação inicializar completamente
- **SMS não funciona**: Normal, Twilio pode estar indisponível
- **Banco não conecta**: Verifique se H2 está habilitado

### Contatos
- **GitHub**: https://github.com/brucantacini/SosLocaliza
- **Documentação**: README.md
- **Testes**: TESTES.md

---

**✅ Projeto pronto para execução e testes no IntelliJ IDEA!**
