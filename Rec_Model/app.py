import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.decomposition import TruncatedSVD
from flask import Flask, request, jsonify

# Load your dataset (make sure it's a cleaned CSV file)
df = pd.read_csv("cleaned_articles.csv")  # Adjust the file path accordingly

df = df.head(10000)

# Clean any missing or null rows
df = df.dropna(subset=['headline', 'category', 'short_description'])

# Concatenate relevant columns to form the content for vectorization
df['content'] = df['headline'] + " " + df['short_description']

# Initialize TF-IDF Vectorizer
tfidf_vectorizer = TfidfVectorizer(stop_words='english')
tfidf_matrix = tfidf_vectorizer.fit_transform(df['content'])

# Apply dimensionality reduction if necessary to reduce memory usage
svd = TruncatedSVD(n_components=100)  # Reducing dimensions to 100 (adjust as needed)
reduced_tfidf_matrix = svd.fit_transform(tfidf_matrix)

# Calculate cosine similarity between articles
cosine_sim = cosine_similarity(reduced_tfidf_matrix, reduced_tfidf_matrix)

# Flask app setup
app = Flask(__name__)

# Function to get article recommendations
def get_recommendations(article_index, top_n=5):
    sim_scores = list(enumerate(cosine_sim[article_index]))
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)  # Sort by similarity score
    sim_scores = sim_scores[1:top_n+1]  # Skip the first one as it will be the article itself

    recommended_articles = []
    for idx, score in sim_scores:
        recommended_articles.append({
            'headline': df['headline'].iloc[idx],
            'link': df['link'].iloc[idx],
            'category': df['category'].iloc[idx],
            'score': score
        })
    return recommended_articles

@app.route('/')
def home():
    return "Flask server is running!"

# API endpoint to get recommendations for a specific article
@app.route('/recommend', methods=['GET'])
def recommend():
    try:
        article_index = int(request.args.get('article_index'))  # Get article index from query parameter
        top_n = int(request.args.get('top_n', 5))  # Number of recommendations to return (default 5)
        recommendations = get_recommendations(article_index, top_n)
        return jsonify(recommendations)
    except Exception as e:
        return jsonify({"error": str(e)})

# Start the Flask server
if __name__ == '__main__':
    app.run(debug=True)

