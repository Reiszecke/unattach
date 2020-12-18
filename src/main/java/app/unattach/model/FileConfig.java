package app.unattach.model;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileConfig implements Config {
  private static final Logger LOGGER = Logger.getLogger(FileConfig.class.getName());
  private static final String DELETE_ORIGINAL_PROPERTY = "delete_original";
  private static final String EMAIL_SIZE_PROPERTY = "email_size";
  private static final String LABEL_IDS_PROPERTY = "label_ids";
  private static final String FILENAME_SCHEMA_PROPERTY = "filename_schema";
  private static final String NUMBER_OF_RUNS_PROPERTY = "number_of_runs";
  private static final String DOWNLOADED_LABEL_ID_PROPERTY = "downloaded_label_id";
  private static final String REMOVED_LABEL_ID_PROPERTY = "removed_label_id";
  private static final String SEARCH_QUERY_PROPERTY = "search_query";
  private static final String TARGET_DIRECTORY_PROPERTY = "target_directory";

  private final Properties config;

  public FileConfig() {
    config = new Properties();
    loadConfigFromFile();
  }

  @Override
  public int getEmailSize() {
    return Integer.parseInt(config.getProperty(EMAIL_SIZE_PROPERTY, "1"));
  }

  @Override
  public boolean getDeleteOriginal() {
    return Boolean.parseBoolean(config.getProperty(DELETE_ORIGINAL_PROPERTY, "true"));
  }

  @Override
  public String getFilenameSchema() {
    return config.getProperty(FILENAME_SCHEMA_PROPERTY, FilenameFactory.DEFAULT_SCHEMA);
  }

  @Override
  public List<String> getLabelIds() {
    return Arrays.asList(config.getProperty(LABEL_IDS_PROPERTY, "").split(","));
  }

  private int getNumberOfRuns() {
    return Integer.parseInt(config.getProperty(NUMBER_OF_RUNS_PROPERTY, "0"));
  }

  @Override
  public String getDownloadedLabelId() {
    return config.getProperty(DOWNLOADED_LABEL_ID_PROPERTY);
  }

  @Override
  public String getRemovedLabelId() {
    return config.getProperty(REMOVED_LABEL_ID_PROPERTY);
  }

  @Override
  public String getSearchQuery() {
    return config.getProperty(SEARCH_QUERY_PROPERTY, "has:attachment size:1m");
  }

  @Override
  public String getTargetDirectory() {
    return config.getProperty(TARGET_DIRECTORY_PROPERTY, getDefaultTargetDirectory());
  }

  @Override
  public int incrementNumberOfRuns() {
    int numberOfRuns = getNumberOfRuns() + 1;
    config.setProperty(NUMBER_OF_RUNS_PROPERTY, Integer.toString(numberOfRuns));
    saveConfigToFile();
    return numberOfRuns;
  }

  @Override
  public void saveFilenameSchema(String schema) {
    config.setProperty(FILENAME_SCHEMA_PROPERTY, schema);
    saveConfigToFile();
  }

  @Override
  public void saveLabelIds(List<String> labelIds) {
    config.setProperty(LABEL_IDS_PROPERTY, String.join(",", labelIds));
    saveConfigToFile();
  }

  @Override
  public void saveDownloadedLabelId(String downloadedLabelId) {
    config.setProperty(DOWNLOADED_LABEL_ID_PROPERTY, downloadedLabelId);
    saveConfigToFile();
  }

  @Override
  public void saveRemovedLabelId(String removedLabelId) {
    config.setProperty(REMOVED_LABEL_ID_PROPERTY, removedLabelId);
    saveConfigToFile();
  }

  @Override
  public void saveSearchQuery(String query) {
    config.setProperty(SEARCH_QUERY_PROPERTY, query);
    saveConfigToFile();
  }

  @Override
  public void saveTargetDirectory(String path) {
    config.setProperty(TARGET_DIRECTORY_PROPERTY, path);
    saveConfigToFile();
  }

  @Override
  public void saveEmailSize(int emailSize) {
    config.setProperty(EMAIL_SIZE_PROPERTY, Integer.toString(emailSize));
    saveConfigToFile();
  }

  @Override
  public void setDeleteOriginal(boolean deleteOriginal) {
    config.setProperty(DELETE_ORIGINAL_PROPERTY, Boolean.toString(deleteOriginal));
    saveConfigToFile();
  }

  private void loadConfigFromFile() {
    File configFile = getConfigPath().toFile();
    if (configFile.exists()) {
      try {
        FileInputStream in = new FileInputStream(configFile);
        config.load(in);
        in.close();
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Failed to load the config file.", e);
      }
    }
  }

  private void saveConfigToFile() {
    try {
      File configFile = getConfigPath().toFile();
      FileOutputStream out = new FileOutputStream(configFile);
      config.store(out, null);
      out.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to save the config file.", e);
    }
  }

  private static Path getConfigPath() {
    String userHome = System.getProperty("user.home");
    return Paths.get(userHome, "." + Constants.PRODUCT_NAME.toLowerCase() + ".properties");
  }

  private static String getDefaultTargetDirectory() {
    String userHome = System.getProperty("user.home");
    Path defaultPath = Paths.get(userHome, "Downloads", Constants.PRODUCT_NAME);
    return defaultPath.toString();
  }
}
