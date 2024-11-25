from flask import Flask, request, jsonify
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.decomposition import TruncatedSVD
from db_connection import fetch_articles  # Import the function from db_connection
import pandas as pd
import os

app = Flask(__name__)

filepath = "cleaned_news_data.csv"

# Step 1: Load the cleaned CSV dataset for training the recommendation model
df_cleaned = pd.read_csv(filepath)  # Path to your cleaned CSV file
df_cleaned = df_cleaned.dropna(axis=1)  # axis=1 means we are dropping columns, not rows

print("Columns in df_cleaned:", df_cleaned.columns)

# Check if the necessary columns are present
if 'headline' not in df_cleaned.columns or 'category' not in df_cleaned.columns:
    raise Exception("The CSV data is missing required columns.")

# Combine text fields to create a content field for vectorization
df_cleaned['content'] = df_cleaned['headline'] + " " + df_cleaned['short_description']

# Step 2: Initialize TF-IDF Vectorizer and calculate the cosine similarity matrix
tfidf_vectorizer = TfidfVectorizer(stop_words='english')
tfidf_matrix = tfidf_vectorizer.fit_transform(df_cleaned['content'])

# Apply dimensionality reduction (optional for large datasets)
svd = TruncatedSVD(n_components=100)  # Adjust the number of components as needed
reduced_tfidf_matrix = svd.fit_transform(tfidf_matrix)

# Calculate cosine similarity matrix
cosine_sim = cosine_similarity(reduced_tfidf_matrix, reduced_tfidf_matrix)

# Step 3: Fetch articles from MongoDB
def fetch_real_time_articles():
    articles = fetch_articles()  # Fetch articles from the MongoDB database
    if not articles:
        raise Exception("No articles found in the database.")
    return articles

# Step 4: Function to get recommendations for a specific article
def get_recommendations(article_index, top_n=5):
    # Retrieve similar articles based on the pre-trained cosine similarity
    sim_scores = list(enumerate(cosine_sim[article_index]))
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)[1:top_n+1]  # Exclude the article itself
    
    # Collect the recommended articles
    recommended_articles = []
    for idx, score in sim_scores:
        recommended_articles.append({
            'headline': df_cleaned['headline'].iloc[idx],
            'link': df_cleaned['link'].iloc[idx],  # Assuming 'link' exists in the CSV
            'category': df_cleaned['category'].iloc[idx],
            'score': score
        })
    return recommended_articles

# Flask routes
@app.route('/')
def home():
    return "Flask server is running!"

@app.route('/recommend', methods=['GET'])
def recommend():
    try:
        article_index = int(request.args.get('article_index'))  # Get the article index from the query parameter
        top_n = int(request.args.get('top_n', 5))  # Number of recommendations to return (default 5)
        
        # Fetch the real-time articles from MongoDB (to show the latest articles)
        articles = fetch_real_time_articles()

        # Get recommendations for the specified article index
        recommendations = get_recommendations(article_index, top_n)
        
        # Add real-time articles to recommendations (optional, based on your business logic)
        for recommendation in recommendations:
            # You can add logic here to adjust based on database fetched articles (e.g., filter by category, etc.)
            pass

        return jsonify(recommendations)
    except Exception as e:
        return jsonify({"error": str(e)})

if __name__ == '__main__':
    app.run(debug=True)
