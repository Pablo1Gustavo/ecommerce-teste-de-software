MUTATION_TEST_REPORT_FILE=target/pit-reports/index.html
COMMON_TEST_REPORT_FILE=target/site/jacoco/index.html

all: help

help:
	@echo "Comandos disponíveis:"
	@echo "  make install               - Limpa, compila, executa os testes e instalar o projeto"
	@echo "  make compile               - Limpa e compila o código-fonte do projeto"
	@echo "  make run                   - Executa a aplicação Spring Boot"
	@echo "  make test                  - Executa os testes (tradicional)"
	@echo "  make test-report           - Abre o relatório de cobertura de testes (JaCoCo)"
	@echo "  make mutation-test         - Executa testes de mutação usando o PIT"
	@echo "  make mutation-test-report  - Abre o relatório de testes de mutação (PIT)"

install:
	mvn clean install

compile:
	mvn clean compile

run:
	mvn spring-boot:run

test:
	mvn clean verify

test-report:
	xdg-open $(COMMON_TEST_REPORT_FILE)

mutation-test:
	mvn org.pitest:pitest-maven:mutationCoverage

mutation-test-report:
	xdg-open $(MUTATION_TEST_REPORT_FILE)