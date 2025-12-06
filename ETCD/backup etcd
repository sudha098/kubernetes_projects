# **🔐 ETCD Backup Guide (Kubernetes Control Plane)**

This guide explains how to take an **etcd snapshot backup** directly on the control plane node and how to copy the backup to another node for safe storage.

---

## 🧩 **1. Backup etcd on the Control Plane Node**

Run the following command **on the control plane** where etcd is running:

```bash
etcdctl --endpoints=https://[127.0.0.1]:2379 \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key \
  snapshot save /opt/snapshot-pre-boot.db
```

**Output:**

```
Snapshot saved at /opt/snapshot-pre-boot.db
```

📌 *This creates a portable etcd snapshot file that can be used for restoration.*

---

## 🧩 **2. Backup etcd on a Cluster With Multiple Nodes**

### **Step 1: Take Snapshot on the Control Plane**

Run this on the control plane node of the cluster:

```bash
etcdctl --endpoints=https://[127.0.0.1]:2379 \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key \
  snapshot save /opt/cluster1.db
```

---

### **Step 2: Copy Snapshot to Another Node**

Run this from the **node where you want to store the backup**:

```bash
scp -r cluster1-controlplane:/opt/cluster1.db /opt/cluster1.db
```

✔ Replace `cluster1-controlplane` with the hostname or IP of the control plane node.

---

## ✅ Notes & Best Practices

* Store snapshots **off the cluster** (jump server, S3 bucket, NAS, etc.)

* Automate backups using cron or systemd timers

* Regularly verify snapshot validity using:

  ```bash
  etcdctl snapshot status /opt/cluster1.db
  ```

* Always back up:

  * `/etc/kubernetes/pki/etcd/*` certificates
  * your etcd snapshot file

