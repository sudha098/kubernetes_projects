
# ✅ **Kubernetes Upgrade Guide (kubeadm)**

### **Upgrade Cluster From v1.24 → v1.25**

> **Important:**
> • Control plane upgraded first
> • Worker nodes upgraded one by one
> • Versions of kubeadm, kubelet, kubectl should match the target version
> • Commands run as **root** or using `sudo`

---

# 🟦 **1. Upgrade Control Plane Node (Master)**

### **Check upgrade availability**

```bash
kubeadm upgrade plan
```

### **Drain the control plane**

```bash
kubectl drain controlplane --ignore-daemonsets --delete-emptydir-data
```

(Optional) Check cluster state:

```bash
kubectl get pods -A -o wide
kubectl get nodes
```

### **Upgrade kubeadm**

```bash
apt-get update
apt-get install -y kubeadm=1.25.0-00
```

### **Apply upgrade**

```bash
kubeadm upgrade apply v1.25.0
```

### **Upgrade kubelet + kubectl**

```bash
apt-get install -y kubelet=1.25.0-00 kubectl=1.25.0-00
systemctl daemon-reload
systemctl restart kubelet
```

### **Verify**

```bash
kubectl get nodes
```

### **Uncordon control-plane**

```bash
kubectl uncordon controlplane
```

---

# 🟩 **2. Upgrade Worker Nodes**

### **SSH into worker**

```bash
ssh node01
```

### **Drain node**

```bash
kubectl drain node01 --ignore-daemonsets --delete-emptydir-data
```

### **Upgrade kubeadm**

```bash
apt-get update
apt-get install -y kubeadm=1.25.0-00
```

### **Upgrade node configuration**

```bash
kubeadm upgrade node
```

### **Upgrade kubelet + kubectl**

```bash
apt-get install -y kubelet=1.25.0-00 kubectl=1.25.0-00
systemctl daemon-reload
systemctl restart kubelet
```

### **Exit node**

```bash
exit
```

### **Uncordon worker**

```bash
kubectl uncordon node01
```

### **Check status**

```bash
kubectl get nodes
```

---

# 🔁 **3. Repeat for each worker node**

Repeat the same steps for `node02`, `node03`, etc.

---

# ✔️ **Upgrade Complete**

