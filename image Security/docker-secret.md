# 🔐 **Create Docker Registry Secret in Kubernetes**

A Docker registry secret allows Kubernetes to authenticate and pull images from private container registries such as Docker Hub, GCR, ECR, Harbor, Nexus, GitHub Container Registry, etc.

Use the following command to create a `docker-registry` type secret:

```bash
kubectl create secret docker-registry my-reg-cred \
  --docker-server=<your-registry-server> \
  --docker-username=<your-username> \
  --docker-password=<your-password> \
  --docker-email=<your-email>
```

---

## 📌 **Parameters Explained**

| Parameter           | Description                                                                                         |
| ------------------- | --------------------------------------------------------------------------------------------------- |
| `--docker-server`   | Registry hostname (e.g., `index.docker.io`, `ghcr.io`, `123456789.dkr.ecr.us-east-1.amazonaws.com`) |
| `--docker-username` | Registry username or service account                                                                |
| `--docker-password` | Token or password for the registry                                                                  |
| `--docker-email`    | Email for the registry account                                                                      |

---

## 📁 **Example: Docker Hub**

```bash
kubectl create secret docker-registry dockerhub-cred \
  --docker-server=https://index.docker.io/v1/ \
  --docker-username=johndoe \
  --docker-password='<PASSWORD_OR_PAT>' \
  --docker-email=john@example.com
```

---

## 📁 **Example: GitHub Container Registry**

```bash
kubectl create secret docker-registry ghcr-cred \
  --docker-server=ghcr.io \
  --docker-username=<github-username> \
  --docker-password=<github-personal-access-token> \
  --docker-email=<email>
```

---

## 🧩 **Use the Secret in a Pod Spec**

You can attach the secret so Kubernetes can pull private images:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: private-image-pod
spec:
  imagePullSecrets:
    - name: my-reg-cred
  containers:
    - name: app
      image: <private-registry>/<image>:tag
```

---

## 🔁 **Set Secret for Entire Namespace (Optional)**

If all pods in a namespace should use the same registry secret:

```bash
kubectl patch serviceaccount default \
  -p '{"imagePullSecrets": [{"name": "my-reg-cred"}]}'
```

---

## ✔ Best Practices

* Prefer **tokens / PATs** instead of passwords
* Use **namespaced secrets** for least privilege
* Rotate credentials periodically
* Use **External Secrets Operator (ESO)** for automation
