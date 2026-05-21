# Millions – IDATx2003 Programmering 2 (vår 2026)

Dette dokumentet inneholder all kontekst om prosjektet og kan ligge i repoet
som `.github/copilot-instructions.md` (Copilot leser den automatisk) eller
`AGENTS.md` i rot.

---

## Hva er dette?

Aksjespill skrevet i Java med JavaFX-GUI. Mappeoppgave i tre deler.
Frist: **27. mai 2026** (utvidet fra 22.05). Karaktervekt: prosjekt 70 %,
rapport 30 %.

I spillet kan en spiller kjøpe og selge andeler i aksjer notert på en børs.
Priser oppdateres ukentlig. Statistikk, nettoverdi og status (Novice /
Investor / Speculator) beregnes underveis.

---

## Tekniske rammer (HARDE krav)

| Komponent | Versjon |
|-----------|---------|
| Java | **25 (LTS)** |
| Build-system | Maven |
| maven-compiler-plugin | 3.14.1 |
| maven-surefire-plugin | 3.5.4 |
| javafx-maven-plugin | 0.0.8 |
| maven-javadoc-plugin | 3.12.0 |
| org.openjfx:javafx-controls | 25.0.1 |
| org.junit.jupiter:junit-jupiter | 6.0.1 |

**Bygging og kjøring må fungere fra kommandolinja:**
- `mvn clean package` (kompilering + tester + JAR)
- `mvn test`
- `mvn javafx:run` (starter GUI)

**Katalogstruktur (standard Maven):**
- `src/main/java` – produksjonskode
- `src/main/resources` – evt. bilder, konfig
- `src/test/java` – enhetstester

**GUI:** JavaFX – det er **ikke lov** å bruke FXML. Programmatisk
view-konstruksjon. Scenebuilder er ok som skisseverktøy.

**Tallbehandling:** Bruk `BigDecimal` (ikke float/double) for alle penger
og kvantiteter. Bruk `BigDecimal`-konstruktør med `String`, ikke `double`,
for å unngå presisjonsfeil:

```java
new BigDecimal("191.27")   // ✅
new BigDecimal(191.27)     // ❌
```

**Sammenligning:** `BigDecimal.compareTo(...)` – ikke `.equals(...)` (skiller
mellom `10.0` og `10.00`).

**Kjedede tester for BigDecimal:** Bruk `assertEquals(expected.compareTo(actual), 0)`
eller egen helper – `assertEquals(BigDecimal, BigDecimal)` feiler på scale-forskjeller.

---

## Domeneklasser (Del 1)

Implementer **nøyaktig disse klassene/signaturene** (lov å utvide modellen
hvis det forbedres arkitektur, men endringer må dokumenteres i rapporten):

### `Stock`
- Felter: `symbol: String`, `company: String`, `prices: List<BigDecimal>`
- Konstruktør: `Stock(String symbol, String company, BigDecimal salesPrice)`
- `getSymbol()`, `getCompany()`, `getSalesPrice()` (siste pris i `prices`),
  `addNewSalesPrice(BigDecimal price)`
- **Del 2-utvidelse:** `getHistoricalPrices()`, `getHighestPrice()`,
  `getLowestPrice()`, `getLatestPriceChange()` (differanse mellom siste og
  nest siste; 0 om kun én pris).

### `Share`
- Felter: `stock: Stock`, `quantity: BigDecimal`, `purchasePrice: BigDecimal`
- Konstruktør: `Share(Stock, BigDecimal quantity, BigDecimal purchasePrice)`
- Standard gettere.

### `Portfolio`
- Felter: `shares: List<Share>`
- `addShare(Share): boolean`, `removeShare(Share): boolean`
- `getShares(): List<Share>`, `getShares(String symbol): List<Share>` (filtrert)
- `contains(Share): boolean`
- **Del 2-utvidelse:** `getNetWorth(): BigDecimal` – total **salgsverdi** for
  alle andeler (bruk `SaleCalculator`).

### `TransactionCalculator` (interface) + `PurchaseCalculator` + `SaleCalculator`
Metoder: `calculateGross()`, `calculateCommission()`, `calculateTax()`,
`calculateTotal()` – alle returnerer `BigDecimal`.

**PurchaseCalculator** (bruker `share.purchasePrice` × `share.quantity`):
- gross = pris × kvantitet
- commission = 0.5 % av gross
- tax = 0
- total = gross + commission + tax

**SaleCalculator** (bruker `stock.salesPrice` × `share.quantity` for gross;
`share.purchasePrice` for kjøpskostnader):
- gross = salesPrice × kvantitet
- commission = 1 % av gross
- tax = 30 % av (gross − commission − (purchasePrice × kvantitet)), MINIMUM 0
- total = gross − commission − tax

...

---

## Kjør prosjektet

```bash
mvn clean install        
mvn test                  
mvn javafx:run            
mvn javadoc:javadoc       
```

---

Se hovedoppgavedokumentet for fullstendig spesifikasjon av alle deler.

