package org.example.newsrecommender.NLP;

import org.apache.commons.csv.*;
import opennlp.tools.doccat.*;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.util.*;

public class ArticleCategorizer {

    private DoccatModel model;

    // Train the model using labeled keywords from a CSV file
    public void trainModel(String trainingCsvPath) throws IOException {
        Reader in = new FileReader(trainingCsvPath);
        CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT.withHeader("link", "headline", "category", "short_description", "authors", "date"));

        List<DocumentSample> trainingSamples = new ArrayList<>();
        for (CSVRecord record : parser) {
            try {
                String category = record.get("category").trim();
                String headline = record.get("headline").trim();

                if (category.isEmpty() || headline.isEmpty() || !isValidCategory(category)) {
                    System.err.println("Skipping invalid record: " + record);
                    continue;
                }

                String[] keywords = extractKeywords(headline).split(",");
                trainingSamples.add(new DocumentSample(category, keywords));
            } catch (IllegalArgumentException e) {
                System.err.println("Error processing record: " + record);
            }
        }

        if (trainingSamples.isEmpty()) {
            throw new IllegalStateException("No valid training samples found.");
        }

        ObjectStream<DocumentSample> sampleStream = new CollectionObjectStream<>(trainingSamples);

        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, 100);
        params.put(TrainingParameters.CUTOFF_PARAM, 1);

        this.model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
        parser.close();
    }

    // Validate categories against known categories
    private boolean isValidCategory(String category) {
        Set<String> validCategories = new HashSet<>(Arrays.asList(
            "U.S. NEWS", "COMEDY", "PARENTING", "WORLD NEWS", "CULTURE & ARTS",
            "TECH", "SPORTS", "ENTERTAINMENT", "POLITICS", "WEIRD NEWS",
            "ENVIRONMENT", "EDUCATION", "CRIME", "SCIENCE", "WELLNESS",
            "BUSINESS", "STYLE & BEAUTY", "FOOD & DRINK", "MEDIA",
            "QUEER VOICES", "HOME & LIVING", "WOMEN", "BLACK VOICES",
            "TRAVEL", "MONEY", "RELIGION", "LATINO VOICES", "IMPACT",
            "WEDDINGS", "COLLEGE", "PARENTS", "ARTS & CULTURE", "STYLE",
            "GREEN", "TASTE", "HEALTHY LIVING", "THE WORLDPOST", "GOOD NEWS",
            "WORLDPOST", "FIFTY", "ARTS", "DIVORCE"
        ));
        return validCategories.contains(category.toUpperCase());
    }

    // Categorize based on extracted keywords
    public String categorize(String headline) throws IOException {
        if (headline == null || headline.trim().isEmpty()) {
            return "UNKNOWN";
        }

        // Extract keywords from the headline
        String[] keywords = extractKeywords(headline).split(",");
        if (keywords.length == 0) {
            return "UNKNOWN";
        }

        // Create the categorizer
        DocumentCategorizerME categorizer = new DocumentCategorizerME(model);

        // Get prediction probabilities for each category
        double[] outcomes = categorizer.categorize(keywords);

        // Determine the best category
        String bestCategory = categorizer.getBestCategory(outcomes);

        // Ensure the predicted category is valid
        if (isValidCategory(bestCategory)) {
            return bestCategory;
        } else {
            return "UNKNOWN";
        }
    }

    // Process a dataset and categorize articles
    // Process a dataset and categorize articles
public void processDataset(String inputCsvPath, String outputCsvPath) throws IOException {
    Reader in = new FileReader(inputCsvPath);
    CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT.withHeader("link", "headline", "category", "short_description", "authors", "date"));
    List<CSVRecord> records = parser.getRecords();

    // Skip the first row (i.e., the first record after the header)
    if (records.size() > 1) {
        records = records.subList(1, records.size());
    }

    // Remove 'authors' and 'date' from the headers
    List<String> headers = new ArrayList<>(parser.getHeaderMap().keySet());
    headers.removeIf(header -> header.trim().equals("authors") || header.trim().equals("date"));

    // Print headers for debugging purposes
    System.out.println("Headers: " + headers);

    // Write output only if valid records are present
    if (!records.isEmpty()) {
        Writer out = new FileWriter(outputCsvPath);
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])));

        for (CSVRecord record : records) {
            // Validate and process the record
            String headline = record.get("headline").trim();
            String category = record.get("category").trim();

            // Skip invalid records
            if (headline.isEmpty() || category.isEmpty()) {
                System.err.println("Skipping invalid record: " + record);
                continue;
            }

            // Predict the category for this headline
            String predictedCategory = categorize(headline);

            // Prepare the output row
            List<String> row = new ArrayList<>(record.toList());

            // Remove 'authors' and 'date' columns safely
            row.removeIf(column -> column.trim().isEmpty());

            int categoryIndex = headers.indexOf("category");

            // Replace the existing category with the predicted category
            if (categoryIndex >= 0) {
                row.set(categoryIndex, predictedCategory);
            }

            // Write the updated record
            printer.printRecord(row);
        }

        parser.close();
        printer.close();
    } else {
        System.err.println("No records found in the input CSV.");
    }
}


    // Keyword extraction using Lucene
    private String extractKeywords(String text) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
        List<String> keywords = new ArrayList<>();

        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            CharTermAttribute attr = tokenStream.getAttribute(CharTermAttribute.class);
            keywords.add(attr.toString());
        }
        tokenStream.end();
        tokenStream.close();
        analyzer.close();

        return String.join(",", keywords);
    }

    public void evaluateModel(String testCsvPath) throws IOException {
        Reader in = new FileReader(testCsvPath);
        CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT.withHeader("category", "headline"));

        int correct = 0;
        int total = 0;
        Map<String, Map<String, Integer>> confusionMatrix = new HashMap<>();
        for (CSVRecord record : parser) {
            String trueCategory = record.get("category").trim();
            String headline = record.get("headline").trim();

            // Skip invalid records
            if (trueCategory.isEmpty() || headline.isEmpty()) continue;

            String keywords = extractKeywords(headline);
            String predictedCategory = categorize(keywords);

            // Count correct predictions
            if (trueCategory.equals(predictedCategory)) {
                correct++;
            }

            // Update confusion matrix
            confusionMatrix.putIfAbsent(trueCategory, new HashMap<>());
            confusionMatrix.get(trueCategory).put(predictedCategory, confusionMatrix.get(trueCategory).getOrDefault(predictedCategory, 0) + 1);

            total++;
        }

        // Calculate accuracy
        double accuracy = (double) correct / total;
        System.out.println("Accuracy: " + accuracy);

        // Print confusion matrix
        System.out.println("Confusion Matrix:");
        confusionMatrix.forEach((trueCategory, predictions) -> {
            System.out.println("True: " + trueCategory);
            predictions.forEach((predictedCategory, count) -> {
                System.out.println("  Predicted: " + predictedCategory + " -> " + count);
            });
        });
    }

    public static void main(String[] args) throws IOException {
        ArticleCategorizer categorizer = new ArticleCategorizer();

        categorizer.trainModel("CSVfiles/training_articles.csv");
        categorizer.evaluateModel("CSVfiles/testing_articles.csv");
        categorizer.processDataset("CSVfiles/articles_for_prediction.csv", "categorized_articles.csv");
        System.out.println("Categorization completed!");
    }
}
