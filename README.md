# 🛒 Catálogo de Produtos (Microsserviço A)

Este repositório contém o microsserviço de **Catálogo**, desenvolvido como parte de um projeto de Sistemas Distribuídos. A aplicação gere o inventário de produtos de hardware e integra-se dinamicamente com um serviço externo de preços.

## 🏗️ Arquitetura do Projeto

O sistema foi desenhado seguindo os princípios de microserviços, focado em alta disponibilidade e prontidão para nuvem (**Cloud-Ready**).

* **Microsserviço A (Catalogo):** Responsável pelos dados cadastrais (ID, Nome, Descrição).
* **Microsserviço B (Preco):** Responsável pelo fornecimento de preços.
* **Integração:** O Catálogo consome o serviço de Preços via HTTP (RestTemplate) utilizando uma estratégia de busca em lote (**Batch Fetching**) para otimizar a performance.

## ☁️ Deploy em AWS (Contexto do Projeto)

A aplicação foi preparada para ser implantada em instâncias **Amazon EC2** dentro de uma **VPC**.
* **Descoberta de Serviço:** O Catálogo utiliza variáveis de ambiente para localizar o IP/DNS do serviço de preços dentro da rede AWS.
* **Monitoramento:** Integra o *Spring Boot Actuator* para fornecer health checks automáticos ao Target Group da AWS.

## 🛠️ Tecnologias Utilizadas

* **Java 26** & **Spring Boot 4.0.5**
* **Spring Data JPA** com banco de dados **H2** (In-memory)
* **SpringDoc OpenAPI (Swagger)** para documentação automática
* **Lombok** para código limpo e conciso
* **Spring Boot Actuator** para monitoramento de saúde da aplicação

## 🚀 Como Executar

### 1. Pré-requisitos
* **Java JDK 17 (ou superior)** instalado.
* **Maven 3.8+** instalado.
* O microsserviço `sd-preco` deve estar em execução (preferencialmente na porta 8081).

### 2. Configuração de Variáveis (Opcional)
Se o serviço de preços não estiver em `localhost`, podes definir o endereço via terminal:
```bash
# Windows (PowerShell)
$env:SD_PRECO_URL="http://ip-da-instancia:8081"

# Linux/Mac/Bash
export SD_PRECO_URL="http://ip-da-instancia:8081"
```

### 3. Passo a Passo para Execução Local
1.  **Clone o repositório:**
    ```bash
    git clone <url-do-teu-repositorio>
    cd sd-catalogo
    ```
2.  **Compile e instale as dependências:**
    ```bash
    mvn clean install
    ```
3.  **Inicie a aplicação:**
    ```bash
    mvn spring-boot:run
    ```
4.  **Acesse o serviço:**
    A aplicação estará disponível em: `http://localhost:8080`

---

## 🌟 Diferenciais Profissionais Implementados

### 1. Resiliência e Graceful Degradation
O sistema foi projetado para não falhar completamente caso o serviço de preços esteja indisponível. Através de um tratamento de exceções robusto, o catálogo continua a servir os dados dos produtos com preço `null`, garantindo que a aplicação não apresente erros fatais ao utilizador.

### 2. Batch Fetching (Eficiência)
Para evitar o problema de performance "N+1", o microsserviço recolhe todos os IDs necessários e faz uma **única chamada** (POST) para procurar todos os preços de uma vez, reduzindo drasticamente a latência de rede.

### 3. Tratamento de Erros Centralizado
Utilização de `@ControllerAdvice` e exceções customizadas (ex: `ProdutoNaoEncontradoException`), garantindo que a API retorne mensagens claras e códigos HTTP semanticamente corretos.

## 📖 Documentação da API

A documentação interativa (Swagger) pode ser acedida em:
`http://localhost:8080/swagger-ui.html`

### Endpoints Principais:
* `GET /produtos`: Lista todos os produtos (integração com serviço de preços).
* `GET /produtos/{id}`: Procura um produto específico por ID.
* `GET /actuator/health`: Status de saúde da aplicação (UP/DOWN).

---
**Desenvolvido para fins acadêmicos - IFSP.**