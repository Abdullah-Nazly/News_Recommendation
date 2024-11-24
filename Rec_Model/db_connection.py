from pymongo import MongoClient
from pymongo.errors import ConnectionError

# Connect to MongoDB (adjust your connection URI)
try:
    client = MongoClient("mongodb://localhost:27017/")  # Adjust this URI if needed
    # Check if we can list databases to confirm the connection
    client.admin.command('ping')  # This sends a ping to confirm the connection
    print("Successfully connected to MongoDB!")

    db = client.recommendation_system  # Replace with your database name

except ConnectionError as e:
    print(f"Failed to connect to MongoDB: {e}")
    exit()

# Fetch articles and user interaction data
def fetch_articles(category=None):
    try:
        if category:
            # Fetch articles by category if specified
            return list(db.articles.find({"category": category}))
        else:
            # Fetch all articles
            return list(db.articles.find())
    except Exception as e:
        print(f"Error fetching articles: {e}")
        return []

def fetch_user_interactions(user_id):
    try:
        # Fetch user interaction data (e.g., category points)
        return db.category_points.find_one({"user_id": user_id})
    except Exception as e:
        print(f"Error fetching user interactions: {e}")
        return None

# Example usage
if __name__ == "__main__":
    articles = fetch_articles("COMEDY")  # Fetch articles for a specific category
    user_interactions = fetch_user_interactions("user123")  # Fetch interactions for a user
    print("Fetched articles:", articles)
    print("Fetched user interactions:", user_interactions)
