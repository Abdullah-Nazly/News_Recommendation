package org.example.newsrecommender;

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

        // Log detailed information
        System.out.println("Headline: " + headline);
        System.out.println("Extracted Keywords: " + Arrays.toString(keywords));
        System.out.println("Prediction Probabilities: " + Arrays.toString(outcomes));
        System.out.println("Predicted Category: " + bestCategory);

        // Ensure the predicted category is valid
        if (isValidCategory(bestCategory)) {
            return bestCategory;
        } else {
            return "UNKNOWN";
        }
    }

    // Process a dataset and categorize articles
    public void processDataset(String inputCsvPath, String outputCsvPath) throws IOException {
        Reader in = new FileReader(inputCsvPath);
        CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT.withHeader("link", "headline", "category", "short_description", "authors", "date"));
        List<CSVRecord> records = parser.getRecords();

        // Keep the same headers but update the "category" column during processing
        List<String> headers = new ArrayList<>(parser.getHeaderMap().keySet());

        // Check if header row exists and write it correctly
        if (records.size() > 0) {
            Writer out = new FileWriter(outputCsvPath);
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])));

            for (CSVRecord record : records) {
                // Read and process the headline
                String headline = record.get("headline");

                // Predict the category for this headline
                String predictedCategory = categorize(headline);

                // Prepare the output row
                List<String> row = new ArrayList<>(record.toList());
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

        categorizer.trainModel("csvFiles/training_articles.csv");
        categorizer.evaluateModel("csvFiles/testing_article.csv");
        categorizer.processDataset("csvFiles/articles_for_prediction.csv", "categorized_articles.csv");
        System.out.println("Categorization completed!");
    }
}
