# Millions

Millions er et aksjespill skrevet i Java og JavaFX for IDATx2003 Programmering 2.
Spilleren starter med kontanter, kjoper og selger aksjeandeler, og folger
utviklingen i priser, portefoljeverdi, nettoverdi og spillerstatus gjennom
ukentlige markedsoppdateringer.

## Teknologi

- Java 25
- Maven
- JavaFX 25.0.1
- JUnit Jupiter 6.0.1

GUI-et er bygget programmatisk i JavaFX. Prosjektet bruker ikke FXML.

## Bygg og test

Kjor kommandoene fra prosjektrota:

```bash
mvn compile
mvn test
mvn package
```

`mvn package` bygger applikasjons-JAR og JavaDoc-JAR. Testene kjores som del av
package-fasen.

## Kjor applikasjonen

```bash
mvn javafx:run
```

## Prosjektstruktur

```text
src/main/java/no/ntnu/idatx2003/millions
├── App.java
├── Main.java
├── controller/
├── exception/
├── io/
├── model/
├── model/transaction/
└── view/
```

## Design

Prosjektet bruker MVC for GUI-flyten:

```text
View -> Controller -> Model
```

Viktige designvalg:

- Strategy for transaksjonskalkulatorer: `TransactionCalculator`
- Strategy for filhandtering: `StockDataReader` og `StockDataWriter`
- Factory for transaksjoner: `TransactionFactory`
- JavaFX `ObservableList` i view-laget for tabelloppdateringer
- Eget Observer-monster for modelloppdateringer fra `Exchange`
- `BigDecimal` for penger og kvantiteter
- JavaFX `LineChart` for prisutvikling paa valgt aksje

## CSV-format

CSV-import forventer tre felt:

```text
# Ticker,Name,Price
NVDA,Nvidia,191.27
AAPL,Apple Inc.,276.43
```

Kommentarlinjer som starter med `#` og blanke linjer ignoreres.
