# 🔄 **ETCD Restore Guide (Stacked & External ETCD)**

This guide explains how to restore ETCD snapshots in both **stacked control plane setups** (etcd running as a static pod) and **external etcd setups** (separate etcd server).
It aligns with best practices recommended for Kubernetes disaster recovery.

---

# 📌 **1. Restore Stacked ETCD (Control Plane Node)**

In a **stacked etcd** setup, etcd runs as a **static pod** on the Kubernetes control plane node under:
`/etc/kubernetes/manifests/etcd.yaml`

---

## **Step 1: Restore the Snapshot**

```bash
etcdctl snapshot restore /opt/snapshot-pre-boot.db \
  --data-dir /var/lib/etcd-from-backup
```

Sample output:

```
mvcc: restore compact to 1231
etcdserver/membership: added member 8e9e05c52164694d [http://localhost:2380]
```

📌 Since the restore is happening **on the same control plane**, the *only required flag* is `--data-dir`.

---

## **Step 2: Update etcd.yaml to Point to New Data Directory**

Edit:

```
/etc/kubernetes/manifests/etcd.yaml
```

Modify the `etcd-data` volume:

```yaml
volumes:
  - hostPath:
      path: /var/lib/etcd-from-backup
      type: DirectoryOrCreate
    name: etcd-data
```

📌 After saving the file, **Kubelet automatically restarts the ETCD static pod** since it's located under the `/etc/kubernetes/manifests` directory.

---

## **Step 3: Wait for ETCD & Control Plane Components to Restart**

Use:

```bash
watch "crictl ps | grep etcd"
```

If ETCD does not become Ready:

```bash
kubectl delete pod -n kube-system etcd-controlplane
```

Wait 1 minute for it to recreate.

---

## **Notes (Important)**

1. **ETCD pod, kube-scheduler, and kube-controller-manager will automatically restart.**
2. You **do not** need to modify `--data-dir` in the YAML unless you want to.
3. If you *do* change `--data-dir`, ensure the matching `volumeMount` is updated. (This is optional.)

---

---

# 📌 **2. Restore External ETCD**

In a **separate etcd server setup**, etcd runs as a systemd service, not as a static pod.

---

## **Step 1: Copy Snapshot to ETCD Server**

From the node where the snapshot is stored:

```bash
scp /opt/cluster2.db etcd-server:/root/cluster2.db
```

---

## **Step 2: Restore Snapshot on the ETCD Server**

```bash
ETCDCTL_API=3 etcdctl \
  --endpoints=https://127.0.0.1:2379 \
  --cacert=/etc/etcd/pki/ca.pem \
  --cert=/etc/etcd/pki/etcd.pem \
  --key=/etc/etcd/pki/etcd-key.pem \
  snapshot restore /root/cluster2.db \
  --data-dir /var/lib/etcd-data-new
```

---

## **Step 3: Update ETCD Systemd Unit File**

Open:

```bash
vim /etc/systemd/system/etcd.service
```

Update the `--data-dir` value to:

```
/var/lib/etcd-data-new
```

---

## **Step 4: Fix Permissions**

```bash
chown -R etcd:etcd /var/lib/etcd-data-new
```

---

## **Step 5: Reload and Restart ETCD**

```bash
systemctl daemon-reload
systemctl restart etcd
```

---

## **Step 6 (Optional but Recommended)**

Restart Kubernetes control plane components to avoid stale state:

```bash
systemctl restart kubelet
```

If scheduler and controller-manager run as static pods, they will restart automatically.

---

# ✅ **Conclusion**

You now have complete restoration workflows for:

✔ **Stacked ETCD (Static Pod)**
✔ **External ETCD (Systemd service)**

This can be directly added to your Kubernetes repo's **Professional Documentation Section**.


