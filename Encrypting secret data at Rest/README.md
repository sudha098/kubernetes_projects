
---

# 🔐 **Kubernetes – Enabling Encryption at Rest (Full Process)**

Encryption at Rest secures Kubernetes Secrets stored in etcd.
This guide shows:

1. Check if encryption is enabled
2. Enable encryption
3. Verify encrypted data
4. Re-encrypt all existing secrets
5. Disable or revert encryption

---

# 1️⃣ **Create a Test Secret (Unencrypted)**

```bash
kubectl create secret generic my-secret \
  --from-literal=key1=supersecret \
  --dry-run=client -o yaml > secret.yaml

kubectl apply -f secret.yaml
```

---

# 2️⃣ **Install etcdctl (if missing)**

For RHEL/CentOS:

```bash
yum install -y etcd-client
```

---

# 3️⃣ **Check if Encryption at Rest is Already Enabled**

### **A. Check etcd directly (secret should appear as plaintext if NOT encrypted)**

```bash
ETCDCTL_API=3 etcdctl \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key \
  get /registry/secrets/default/my-secret | hexdump -C
```

If secret value appears in **plaintext**, encryption is **NOT** enabled.

---

### **B. Check kube-apiserver arguments**

```bash
ps aux | grep kube-apiserver | grep -- --encryption-provider-config
```

### **C. Inspect static pod manifest**

```bash
cat /etc/kubernetes/manifests/kube-apiserver.yaml
```

No encryption config = **not encrypted**.

---

# 4️⃣ **Enable Encryption at Rest**

### **A. Generate a 32-byte encryption key**

```bash
head -c 32 /dev/urandom | base64
```

Use this value in your `encryption-config.yaml` (example file):

**enc.yaml:**

```yaml
apiVersion: apiserver.config.k8s.io/v1
kind: EncryptionConfiguration
resources:
  - resources:
      - secrets
    providers:
      - aescbc:
          keys:
            - name: key1
              secret: <BASE64_KEY_HERE>
      - identity: {}
```

Apply encryption config locally:

```bash
kubectl apply -f enc.yaml
```

---

### **B. Move encryption config to correct directory**

```bash
mkdir -p /etc/kubernetes/enc
mv encryption-config.yaml /etc/kubernetes/enc/enc.yaml
ls /etc/kubernetes/enc
```

---

### **C. Modify the kube-apiserver manifest**

Edit:

```bash
vim /etc/kubernetes/manifests/kube-apiserver.yaml
```

Add to **command section**:

```
- --encryption-provider-config=/etc/kubernetes/enc/enc.yaml
```

Add to **volumeMounts**:

```yaml
- name: enc
  mountPath: /etc/kubernetes/enc
  readOnly: true
```

Add to **volumes**:

```yaml
- name: enc
  hostPath:
    path: /etc/kubernetes/enc
    type: DirectoryOrCreate
```

### **APIServer will auto-restart**

(Static pod watched by kubelet.)

Check:

```bash
kubectl get pods -n kube-system
crictl pods
```

---

# 5️⃣ **Verify Encryption is Working**

### **A. Confirm APIServer is using encryption**

```bash
ps aux | grep kube-apiserver | grep -- --encryption-provider-config
```

### **B. Create a NEW secret (should be encrypted)**

```bash
kubectl create secret generic my-secret-2 \
  --from-literal=key2=topsecret \
  --dry-run=client -o yaml > secret2.yaml

kubectl apply -f secret2.yaml
```

### **C. Read the secret from etcd**

```bash
ETCDCTL_API=3 etcdctl \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key \
  get /registry/secrets/default/my-secret-2 | hexdump -C
```

If the value is unreadable (garbled bytes), encryption is **working**.

---

# 6️⃣ **Encrypt ALL Existing Secrets**

Older secrets (created before encryption) remain unencrypted until rewritten.

Force rewrite:

```bash
kubectl get secrets --all-namespaces -o json | kubectl replace -f -
```

---

# 7️⃣ **Disable Encryption at Rest (Revert)**

Modify your encryption config so that:

`identity` is **first** provider:

```yaml
providers:
  - identity: {}
  - aescbc:
      keys:
      - name: key1
        secret: <BASE64_KEY>
```

Restart kube-apiserver (auto restart as static pod or manually if needed).

Now secrets will be stored in plaintext again.

---

# 📌 Best Practices for Kubernetes Secrets

Even without encryption:

1. **Never commit secret manifests to Git**
2. **Enable Encryption at Rest** (which we just implemented)
3. Use **RBAC** to restrict access
4. Use **external secret stores** (Vault / AWS KMS / GCP KMS / Azure Key Vault)
5. Use **sealed-secrets** or **SOPS** when storing secrets in git

---

