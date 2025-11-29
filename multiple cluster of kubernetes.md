
# ✅ Working with Multiple Kubernetes Clusters

### **Check configured clusters**

```bash
kubectl config get-clusters
```

Output:

```
NAME
cluster1
cluster2
```

---

# 🔄 Switch kubeconfig Context

### **Switch context to cluster1**

```bash
kubectl config use-context cluster1
```

Verify:

```bash
kubectl get nodes
```

---

# 🧩 Identifying etcd Type (Stacked vs External)

### SSH into the controlplane:

```bash
ssh cluster1-controlplane
```

### Check pods (for **stacked etcd**):

```bash
kubectl get pods -A
```

If you see pod `etcd-controlplane` → **stacked etcd**.

---

### Check running processes (for **external etcd**):

```bash
ps -ef | grep etcd
```

If etcd is running as a standalone systemd service → **external etcd**.

---

# 🧷 etcd Backup (Stacked or External)

## **On the cluster controlplane:**

```bash
ETCDCTL_API=3 etcdctl \
  --endpoints=https://127.0.0.1:2379 \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key \
  snapshot save /opt/cluster1.db
```

---

## **Copy backup to another node**

On **remote node**:

```bash
scp cluster1-controlplane:/opt/cluster1.db /opt/cluster1.db
```

---

# 🏗 Restoring External etcd Snapshot

> **Scenario:** Restoring a snapshot for **cluster2** running external etcd.

---

### **Step 1 — Copy snapshot to etcd server**

```bash
scp /opt/cluster2.db etcd-server:/root/cluster2.db
```

---

### **Step 2 — Restore snapshot on etcd-server**

Use a *new* data directory so the restore doesn’t overwrite live data yet.

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

### **Step 3 — Update systemd service file**

Edit the etcd unit:

```bash
vim /etc/systemd/system/etcd.service
```

Update:

```
--data-dir=/var/lib/etcd-data-new
```

Save and exit.

---

### **Step 4 — Fix permissions**

```bash
chown -R etcd:etcd /var/lib/etcd-data-new
```

---

### **Step 5 — Reload & restart etcd**

```bash
systemctl daemon-reload
systemctl restart etcd
```

Check status:

```bash
systemctl status etcd
```

---

### **Step 6 (Recommended) — Restart control plane components**

Restart components that rely on etcd state:

```bash
systemctl restart kube-apiserver
systemctl restart kube-controller-manager
systemctl restart kube-scheduler
systemctl restart kubelet
```

---

# ✔️ Process Summary

| Task                | Command                                |
| ------------------- | -------------------------------------- |
| Identify clusters   | `kubectl config get-clusters`          |
| Switch context      | `kubectl config use-context <cluster>` |
| Backup etcd         | `etcdctl snapshot save FILE`           |
| Restore etcd        | `etcdctl snapshot restore FILE`        |
| Update etcd systemd | edit `etcd.service`                    |
| Restart services    | `systemctl restart etcd`               |

---


