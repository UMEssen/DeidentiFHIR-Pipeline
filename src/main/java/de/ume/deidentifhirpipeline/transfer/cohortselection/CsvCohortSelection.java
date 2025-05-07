package de.ume.deidentifhirpipeline.transfer.cohortselection;

import de.ume.deidentifhirpipeline.config.ProjectConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

@Component("cohort-selection.via-csv")
public class CsvCohortSelection implements CohortSelection {
  @Override
  public List<String> before(ProjectConfig projectConfig) throws Exception {
    String path = projectConfig.getCohortSelection().getViaCsv().getPath();
    String delimiter = projectConfig.getCohortSelection().getViaCsv().getDelimiter();
    String columnName = projectConfig.getCohortSelection().getViaCsv().getColumnName();
    Integer columnNumber = projectConfig.getCohortSelection().getViaCsv().getColumnNumber();
    boolean skipFirstRow = projectConfig.getCohortSelection().getViaCsv().isSkipFirstRow();

    CSVFormat.Builder builder = CSVFormat.DEFAULT.builder()
        .setDelimiter(delimiter)
        .setIgnoreSurroundingSpaces(true)
        .setIgnoreEmptyLines(true)
        .setAllowMissingColumnNames(true);

    if (columnNumber == null || skipFirstRow) {
      builder = builder
          .setHeader()
          .setSkipHeaderRecord(true);
    }

    try (Reader reader = new FileReader(path); CSVParser csvParser = CSVParser.builder().setReader(reader).setFormat(builder.get()).get()) {
      if (columnNumber != null)
        return byColumnNumber(csvParser, columnNumber);
      return byColumnName(csvParser, columnName);
    }
  }

  private static List<String> byColumnName(CSVParser csvParser, String columnName) {
    return csvParser.getRecords().stream()
        .map(record -> record.get(columnName))
        .toList();
  }

  private static List<String> byColumnNumber(CSVParser csvParser, int columnNumber) {
    return csvParser.getRecords().stream()
        .map(record -> record.get(columnNumber))
        .toList();
  }
}
