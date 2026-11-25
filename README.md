# CPU Cache Hierarchy Impact: Row-Major vs. Column-Major Matrix Traversal

Empirical benchmark measuring memory access latency across L1d, L2, L3 caches, and main memory (RAM) on an **Intel Core i9-11900H** processor.

## Hardware & Environment
- **CPU:** 11th Gen Intel Core i9-11900H (8 Cores / 16 Threads)
- **L1d Cache:** 48 KB per core (384 KB total)
- **L2 Cache:** 1.25 MB per core (10 MB total)
- **L3 Cache:** 24 MB shared
- **Runtime:** OpenJDK 27 (`-Xms1G -Xmx1G`)
- **Analysis:** Python 3.12 (`matplotlib`, `statistics`, `csv`)

## Repository Contents
- `CacheBenchmark.java` — Java benchmark using a contiguous 1D array (`int[N*N]`), JIT warm-up (3 runs), 15 measured runs per size, and a `volatile` blackhole to prevent dead-code elimination.
- `results.csv` — Raw experimental measurements (135 rows, `Locale.US` formatting).
- `analyze.py` — Statistical aggregation script (Mean, Standard Deviation, Slowdown ratio) and plot generator.
- `cache_hierarchy_plot.png` — Log-log plot of access latency vs. matrix size with hardware cache boundaries.

## Reproduction Instructions
```bash
# 1. Compile and run the benchmark (generates results.csv)
javac CacheBenchmark.java
java -Xms1G -Xmx1G CacheBenchmark

# 2. Install plotting dependency and run analysis
pip install matplotlib
python analyze.py

```
