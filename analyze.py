import csv, math, statistics
import matplotlib.pyplot as plt

data = {}
with open("results.csv", newline="") as f:
    for row in csv.DictReader(f):
        n = int(row["N"])
        kb = float(row["SizeKB"])
        data.setdefault((n, kb), {"row": [], "col": []})
        data[(n, kb)]["row"].append(float(row["RowMajorNs"]))
        data[(n, kb)]["col"].append(float(row["ColMajorNs"]))

print(f"{'N':>5} | {'Size (KB)':>10} | {'Row Mean±SD (ns)':>18} | {'Col Mean±SD (ns)':>18} | {'Slowdown':>8}")
print("-" * 70)

sizes_kb, row_means, col_means, row_sds, col_sds = [], [], [], [], []
for (n, kb), vals in sorted(data.items()):
    r_m, r_sd = statistics.mean(vals["row"]), statistics.stdev(vals["row"])
    c_m, c_sd = statistics.mean(vals["col"]), statistics.stdev(vals["col"])
    ratio = c_m / r_m
    print(f"{n:>5} | {kb:>10.1f} | {r_m:>8.4f} ± {r_sd:<7.4f} | {c_m:>8.4f} ± {c_sd:<7.4f} | {ratio:>7.2f}x")
    sizes_kb.append(kb)
    row_means.append(r_m); row_sds.append(r_sd)
    col_means.append(c_m); col_sds.append(c_sd)

plt.figure(figsize=(10, 6), dpi=300)
plt.errorbar(sizes_kb, row_means, yerr=row_sds, marker='o', capsize=4, linewidth=2, label='Row-Major (Sequential)')
plt.errorbar(sizes_kb, col_means, yerr=col_sds, marker='s', capsize=4, linewidth=2, label='Column-Major (Strided)')

plt.axvline(48, color='green', linestyle='--', alpha=0.7, label='L1d Cache (48 KB/core)')
plt.axvline(1280, color='orange', linestyle='--', alpha=0.7, label='L2 Cache (1.25 MB/core)')
plt.axvline(24576, color='red', linestyle='--', alpha=0.7, label='L3 Cache (24 MB shared)')

plt.xscale('log', base=2)
plt.yscale('log')
plt.xlabel('Matrix Size (KB) — Log2 Scale', fontsize=11)
plt.ylabel('Access Time per Element (ns) — Log Scale', fontsize=11)
plt.title('CPU Cache Hierarchy Impact: Row-Major vs Column-Major (Intel i9-11900H)', fontsize=12)
plt.grid(True, which="both", linestyle=":", alpha=0.5)
plt.legend()
plt.tight_layout()
plt.savefig("cache_hierarchy_plot.png")
print("\nSaved plot to cache_hierarchy_plot.png")
