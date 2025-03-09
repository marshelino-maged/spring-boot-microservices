import requests
import csv

API_KEY = "b0af84324ea41ca39f4311c9156e94e3"
BASE_URL = "https://api.themoviedb.org/3/movie/popular"
MOVIES_TO_FETCH = 1000
MOVIES_PER_PAGE = 20
TOTAL_PAGES = MOVIES_TO_FETCH // MOVIES_PER_PAGE  # 1000/20 = 50 pages
OUTPUT_FILE = "movie_ids.csv"

def fetch_movie_ids():
    movie_ids = []
    
    for page in range(1, TOTAL_PAGES + 1):
        url = f"{BASE_URL}?api_key={API_KEY}&page={page}"
        response = requests.get(url)

        if response.status_code == 200:
            data = response.json()
            movies = data.get("results", [])
            movie_ids.extend([movie["id"] for movie in movies])
        else:
            print(f"Error fetching page {page}: {response.status_code}")
            break
    
    return movie_ids[:MOVIES_TO_FETCH]  # Ensure only 1000 IDs

def save_to_csv(movie_ids):
    with open(OUTPUT_FILE, mode="w", newline="") as file:
        writer = csv.writer(file)
        writer.writerow(["movie_id"])
        for movie_id in movie_ids:
            writer.writerow([movie_id])
    
    print(f"✅ Successfully saved {len(movie_ids)} movie IDs to {OUTPUT_FILE}")

# Run the script
movie_ids = fetch_movie_ids()
save_to_csv(movie_ids)
