# **Kubernetes Projects & Manifests Repository**

This repository contains a collection of Kubernetes manifests, configuration examples, and hands-on project files designed to help understand, practice, and implement core Kubernetes concepts.
It serves as a reference for developers, DevOps engineers, platform engineers, and Kubernetes learners who want practical examples of real-world cluster operations.

---

## **📁 Repository Structure**

### **🧩 Core Kubernetes Concepts**

| Topic                                   | Description                                                               |
| --------------------------------------- | ------------------------------------------------------------------------- |
| **Command and Arguments**               | Examples of defining container commands, args, and environment variables. |
| **Environment Variables**               | Creating and using environment variables & Kubernetes Secrets.            |
| **Resource Limits**                     | CPU/memory requests and limits for workloads.                             |
| **Network Policy**                      | Sample network policy and security context manifests.                     |
| **Static Pod**                          | Example of running static pods via Kubelet.                               |
| **Init Containers**                     | Using init containers for pre-start tasks.                                |
| **Node Affinity Examples**              | Affinity/anti-affinity scheduling examples.                               |
| **Pod Security Context & Capabilities** | Demonstrates Linux capabilities and pod-level security.                   |

---

### **🔐 Security & Secrets**

| Topic                                 | Description                                                |
| ------------------------------------- | ---------------------------------------------------------- |
| **Encrypting Secret Data at Rest**    | Instructions and examples for enabling encryption at rest. |
| **Certificate Signing Request (CSR)** | CSR examples for cluster certificate management.           |
| **Image Security**                    | Using private image registries and imagePullSecrets.       |

---

### **⚙️ Cluster-Level Components**

| Topic                         | Description                                                                 |
| ----------------------------- | --------------------------------------------------------------------------- |
| **Multiple Scheduler**        | Definitions and configuration for running additional Kubernetes schedulers. |
| **ETCD**                      | ETCD manifests and examples.                                                |
| **Cluster Upgrade (kubeadm)** | Guides for upgrading Kubernetes clusters.                                   |
| **Multiple Cluster Setup**    | Instructions for managing multiple clusters.                                |

---

### **📦 Workload Deployments**

| Topic                    | Description                                           |
| ------------------------ | ----------------------------------------------------- |
| **Deployment Examples**  | Nginx, multi-container pods, NodePort services, etc.  |
| **DaemonSets**           | Example DaemonSet definitions.                        |
| **3-Tier Demo Projects** | Example 3-tier application deployments.               |
| **Nagios Deployment**    | Monitoring tool deployment for cluster health checks. |

---

### **📊 Observability Stack**

| Topic                              | Description                                            |
| ---------------------------------- | ------------------------------------------------------ |
| **demo-3tier-observability-stack** | 3-tier app with observability integrated.              |
| **grafana-prometheus-stack**       | Files to deploy monitoring using Prometheus & Grafana. |
| **k8s-3tier-observability-demo**   | Complete demo with dashboards and metrics.             |

---

## **📘 Kubernetes Documentation**

For official Kubernetes docs:
➡️ [https://kubernetes.io/docs/home/](https://kubernetes.io/docs/home/)

---

## **🚀 How to Use This Repository**

```bash
# Clone the repository
git clone https://github.com/<your-username>/kubernetes_projects.git
cd kubernetes_projects

# Apply any manifest
kubectl apply -f <manifest.yml>

# Delete a resource
kubectl delete -f <manifest.yml>
```

Each folder includes relevant YAML manifests and, where applicable, README notes explaining concepts and usage.

---

## **🧠 Upcoming Additional Topics**

### **1️⃣ Ingress & Advanced Networking**

* Ingress resource examples
* NGINX / Traefik ingress controller setup
* TLS termination examples
* LoadBalancer service integration

### **2️⃣ Persistent Storage (Advanced)**

* StatefulSets with PVC templates
* Dynamic provisioning using StorageClasses
* CSI driver examples (EBS, GCE PD, Azure Disk)

### **3️⃣ ConfigMaps + Secrets Best Practices**

* Mounted vs env
* Secret encryption using KMS
* ExternalSecret Operator examples

### **4️⃣ RBAC & Security**

* RBAC for service accounts
* OPA Gatekeeper policies
* Pod Security Admission (PSA) examples
* Runtime security (Falco, Kyverno policies)

### **5️⃣ Autoscaling**

* HPA (CPU, Memory, Custom metrics)
* VPA (Vertical Pod Autoscaler)
* Cluster Autoscaler on cloud providers

### **6️⃣ Service Mesh**

* Istio demo installation
* mTLS setup
* Traffic shifting + canary deployments

### **7️⃣ GitOps**

* ArgoCD application definitions
* Flux CD examples
* CI/CD to Kubernetes GitOps workflow

### **8️⃣ Logging & Monitoring (Advanced)**

* Loki + Promtail setup
* OpenTelemetry Collector
* Custom Grafana dashboards

### **9️⃣ Helm Packaging**

* Convert several YAML examples into Helm charts
* Chart structure + values.yaml customization

### **🔟 Kubernetes Operators**

* Simple Operator using Kubebuilder or Operator SDK
* CRD examples

### **1️⃣1️⃣ KEDA (Event Driven Autoscaling)**

* Autoscaling based on Kafka, Prometheus, or HTTP triggers

### **1️⃣2️⃣ Backup & Disaster Recovery**

* Velero backup/restore setup
* ETCD backup automation

