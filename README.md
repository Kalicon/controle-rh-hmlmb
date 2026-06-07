<div align="center">

# 🏥 Controle RH - HMLMB

### Sistema de Gestão de Férias e Requerimentos de RH

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://supabase.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template%20Engine-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![Python](https://img.shields.io/badge/Python-3.x-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)

> Sistema web desenvolvido para facilitar o controle de férias dos funcionários do **Hospital Maternidade Leonor Mendes de Barros - HMLMB**, automatizando o lançamento via SGP e o gerenciamento de requerimentos.

</div>

---

## 📋 Sobre o Projeto

O **Controle RH HMLMB** é uma aplicação web interna desenvolvida para o setor de Recursos Humanos do hospital. O sistema centraliza o controle de férias, elimina planilhas manuais e automatiza a importação de dados a partir de PDFs oficiais.

### ✨ Funcionalidades Principais

| Módulo | Descrição |
|--------|-----------|
| 🏠 **Dashboard** | Visão geral com KPIs: total de funcionários, férias ativas, requerimentos e pendências |
| 👥 **Funcionários** | Cadastro completo de servidores com dados de cargo, regime jurídico e período de vigência |
| 📄 **Requerimentos** | Lançamento e acompanhamento de solicitações de férias por período |
| 🤖 **Automação PDF** | Importação automática de dados de férias via leitura de PDFs (script Python + pdfplumber) |
| 🔐 **Controle de Acesso** | Autenticação com Spring Security, perfis de Admin e Funcionário RH |
| 👤 **Gestão de Usuários** | Cadastro e gerenciamento de usuários do sistema (somente Admin) |

---

## 🛠️ Stack Tecnológica

### Backend
- **Java 17** + **Spring Boot 3.3**
- **Spring Security** — Autenticação e autorização por perfil (ROLE_ADMIN / ROLE_USER)
- **Spring Data JPA** + **Hibernate** — Persistência de dados
- **Apache POI** — Leitura de planilhas Excel

### Frontend
- **Thymeleaf** — Template engine server-side
- **Bootstrap 5.3** — Framework CSS responsivo
- **Font Awesome 6** — Ícones
- **Chart.js** — Gráficos interativos no dashboard

### Banco de Dados
- **PostgreSQL** via **Supabase** (produção)
- **H2** (desenvolvimento local, em memória)

### Automação
- **Python 3** + **pdfplumber** — Extração de dados de PDFs de férias

---

## 🚀 Como Executar Localmente

### Pré-requisitos

- [Java 17+](https://adoptium.net/)
- [Maven 3.6+](https://maven.apache.org/) (ou use o `mvnw` incluído)
- [Python 3.x](https://www.python.org/) (para a automação PDF)

### 1. Clone o repositório

```bash
git clone https://github.com/Kalicon/controle-rh-hmlmb.git
cd controle-rh-hmlmb
```

### 2. Configure o banco de dados local

Copie o arquivo de exemplo e ajuste conforme necessário:

```bash
cp src/main/resources/application-local.properties.example \
   src/main/resources/application-local.properties
```

> O perfil local usa **H2 em memória** — nenhuma instalação de banco de dados é necessária para testar!

### 3. Execute a aplicação

```bash
# Usando o Maven Wrapper incluído (recomendado)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Ou com Maven instalado
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 4. Acesse no navegador

```
http://localhost:8080
```

### 5. Instale as dependências Python (opcional — para automação PDF)

```bash
cd scripts
pip install -r requirements.txt
```

---

## ⚙️ Configuração de Produção

As credenciais do banco de dados são lidas de **variáveis de ambiente**. Configure no seu servidor:

```bash
export DB_URL=jdbc:postgresql://seu-host:5432/postgres
export DB_USERNAME=postgres
export DB_PASSWORD=sua_senha_segura
```

Ou via arquivo `.env` na sua plataforma de hospedagem (Railway, Render, etc.).

> ⚠️ **Nunca commite senhas diretamente no código!** Use sempre variáveis de ambiente.

---

## 📁 Estrutura do Projeto

```
controle-rh-hmlmb/
├── src/
│   └── main/
│       ├── java/com/hmlmb/rh/
│       │   ├── config/          # Configurações de segurança
│       │   ├── controller/      # Controllers Spring MVC
│       │   ├── dto/             # Data Transfer Objects
│       │   ├── model/           # Entidades JPA
│       │   ├── repository/      # Repositórios Spring Data
│       │   └── service/         # Lógica de negócio
│       └── resources/
│           ├── templates/       # Templates Thymeleaf
│           │   ├── fragments/   # Componentes reutilizáveis (menu, header)
│           │   ├── funcionarios/
│           │   ├── requerimentos/
│           │   └── automacao/
│           └── application.properties
├── scripts/
│   ├── extrair_dados.py         # Script Python para leitura de PDF
│   └── requirements.txt
├── pom.xml
└── README.md
```

---

## 🔑 Perfis de Acesso

| Perfil | Permissões |
|--------|-----------|
| `ROLE_ADMIN` | Acesso total: gestão de usuários, todos os módulos |
| `ROLE_USER` | Dashboard, funcionários, requerimentos e automação |

---

## 🤝 Contribuindo

Este é um projeto interno do HMLMB. Para sugestões ou bugs, abra uma [Issue](https://github.com/Kalicon/controle-rh-hmlmb/issues).

---

<div align="center">

**Desenvolvido para o Hospital Maternidade Leonor Mendes de Barros - HMLMB**

© 2024 Sistema de Controle RH · Todos os direitos reservados

</div>
