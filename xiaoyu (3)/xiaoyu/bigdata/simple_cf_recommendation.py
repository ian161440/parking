"""Simple collaborative filtering style recommendation example for the parking system.

This script is intended for *demonstration* in the graduation project. It shows how
one could:
- Read historical reservation data from MySQL.
- Build a simple user-space interaction matrix.
- Compute naive user-based similarity and recommend parking spaces that a user
  has not used before.

It is NOT meant to be a production-grade algorithm, but rather a clear and
lightweight educational example.
"""

import math
from collections import defaultdict

import numpy as np
import pymysql


# ---------------------------------------------------------------------------
# Database configuration (adjust to match your local environment)
# ---------------------------------------------------------------------------
DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "4499",  # adjust if needed
    "database": "parking_system",
    "charset": "utf8mb4",
}


def fetch_user_space_counts():
    """Fetch aggregated reservation counts per (user_id, space_id).

    Returns
    -------
    interactions : dict[int, dict[int, float]]
        interactions[user_id][space_id] = count
    """

    conn = pymysql.connect(**DB_CONFIG)
    try:
        with conn.cursor() as cur:
            sql = (
                "SELECT user_id, space_id, COUNT(*) AS cnt "
                "FROM reservation "
                "GROUP BY user_id, space_id"
            )
            cur.execute(sql)
            interactions: dict[int, dict[int, float]] = defaultdict(dict)
            for user_id, space_id, cnt in cur.fetchall():
                if user_id is None or space_id is None:
                    continue
                interactions[int(user_id)][int(space_id)] = float(cnt)
    finally:
        conn.close()

    return interactions


def cosine_similarity(vec_a: dict[int, float], vec_b: dict[int, float]) -> float:
    """Compute cosine similarity between two sparse vectors (as dicts)."""

    # intersection of keys
    common_keys = set(vec_a.keys()) & set(vec_b.keys())
    if not common_keys:
        return 0.0

    dot = sum(vec_a[k] * vec_b[k] for k in common_keys)
    norm_a = math.sqrt(sum(v * v for v in vec_a.values()))
    norm_b = math.sqrt(sum(v * v for v in vec_b.values()))
    if norm_a == 0 or norm_b == 0:
        return 0.0

    return dot / (norm_a * norm_b)


def build_user_similarities(interactions: dict[int, dict[int, float]]):
    """Compute pairwise user similarities.

    Returns
    -------
    sims : dict[int, dict[int, float]]
        sims[u][v] = similarity between user u and user v
    """

    users = list(interactions.keys())
    sims: dict[int, dict[int, float]] = defaultdict(dict)

    for i, u in enumerate(users):
        for v in users[i + 1 :]:
            sim = cosine_similarity(interactions[u], interactions[v])
            if sim <= 0:
                continue
            sims[u][v] = sim
            sims[v][u] = sim

    return sims


def recommend_for_user(
    target_user: int,
    interactions: dict[int, dict[int, float]],
    sims: dict[int, dict[int, float]],
    top_k_neighbors: int = 5,
    top_n_items: int = 10,
):
    """Recommend parking spaces for a given user based on similar users.

    Parameters
    ----------
    target_user : int
        User ID to recommend for.
    interactions : dict[int, dict[int, float]]
        Historical user-space counts.
    sims : dict[int, dict[int, float]]
        User-user similarities.
    top_k_neighbors : int
        How many most similar neighbors to consider.
    top_n_items : int
        How many items (spaces) to recommend.

    Returns
    -------
    list[tuple[int, float]]
        List of (space_id, score) sorted by score descending.
    """

    if target_user not in interactions:
        return []

    # already used spaces by the target user
    used_spaces = set(interactions[target_user].keys())

    # similar users sorted by similarity
    neighbors = sorted(sims.get(target_user, {}).items(), key=lambda x: x[1], reverse=True)[
        :top_k_neighbors
    ]

    scores: dict[int, float] = defaultdict(float)

    for neighbor_id, sim in neighbors:
        for space_id, cnt in interactions[neighbor_id].items():
            if space_id in used_spaces:
                continue
            # simple weighted sum: similarity * interaction count
            scores[space_id] += sim * cnt

    ranked = sorted(scores.items(), key=lambda x: x[1], reverse=True)[:top_n_items]
    return ranked


def main():
    print("[INFO] Fetching user-space interaction data from MySQL...")
    interactions = fetch_user_space_counts()
    if not interactions:
        print("[WARN] No reservation data found. Please create some reservations first.")
        return

    print(f"[INFO] Loaded interactions for {len(interactions)} users.")

    print("[INFO] Computing user-user similarities...")
    sims = build_user_similarities(interactions)

    print("[INFO] Sample recommendations (up to 3 users):")
    for i, user_id in enumerate(sorted(interactions.keys())):
        if i >= 3:
            break
        recs = recommend_for_user(user_id, interactions, sims)
        print(f"\nUser {user_id} recommendations (space_id, score):")
        if not recs:
            print("  (no recommendations - maybe this user has too few neighbors)")
            continue
        for space_id, score in recs:
            print(f"  {space_id}\t{score:.4f}")


if __name__ == "__main__":
    main()
