# Teste de Software - E-commerce

Este é um trabalho prático da disciplina de Teste de Software que consiste na implementação de um caso de uso dentro de um sistema simples de e-commerce para ser validado com testes de mutação.

---

### **Componentes do grupo**

- Pablo Gustavo Fernandes Maia  
- Pablo Deyvid de Paiva  
- Gabriel Ribeiro Barbosa da Silva  

**Professor:** Eiji Adachi Medeiros Barbosa

---

### **Tecnologias utilizadas**

- **Java**: Linguagem de programação principal.  
- **Spring Boot**: Framework para desenvolvimento da API REST.  
- **JUnit 5**: Framework para execução de testes unitários.  
- **PIT**: Ferramenta para geração de testes de mutação.  

---

### **Requisitos recomendados**

- **Apache Maven**: Versão **3.8** ou superior.  
- **Java**: Versão **17** ou superior.  
- **Make**: (Opcional) para facilitar a execução de comandos via `Makefile`.

---

### **Instruções**

Na raiz do projeto, você pode utilizar os seguintes comandos:
OBS: Caso não tenha acesso aos comandos do Makefile verifique a estrutura e execute manualmente.

**Ajuda**
Obter instruções gerais sobre os comandos disponíveis no Makefile:
```bash
make
```

**Instalar**
```bash
make install
```

**Compilar**
```bash
make compile
```

**Executar a aplicação**
```bash
make run
```

**Executar os testes**
```bash
make test
```

**Abrir o relatório de cobertura de testes (JaCoCo)**
Após executar os testes, abra o relatório de cobertura:
```bash
make test-report
```
Ou abra manualmente em seu navegador pelo arquivo `target/site/jacoco/index.html`

**Executar os testes de mutação (PIT)**
Rodar o framework PIT para validação com testes de mutação:
```bash
make mutation-test
```

#### **Abrir o relatório de testes de mutação (PIT)**
Após rodar os testes de mutação, abra o relatório:
```bash
make mutation-test-report
```
Ou abra manualmente em seu navegador pelo arquivo `target/pit-reports/index.html`
---
