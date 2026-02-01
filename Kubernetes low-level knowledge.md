# Kubernetes low-level mental model

At its core, **Kubernetes** is:

> A **distributed control system** that continuously reconciles **desired state** → **actual state**

Everything else is an implementation detail.

---

## 1️⃣ Control Plane (the brain)

The control plane **does not run containers**.
It only makes **decisions** and **records state**.

---

### 🔹 etcd (source of truth)

* Distributed **key–value store**
* Stores **all cluster state**
* Strongly consistent (Raft)

Contains:

* Pods
* Deployments
* Secrets
* ConfigMaps
* Node status
* Everything you `kubectl get`

Key facts:

* If etcd is lost → **cluster is lost**
* Control plane components are **stateless without etcd**
* Backups are **non-optional** in prod

---

### 🔹 kube-apiserver (the front door)

* REST API
* All reads/writes go through it
* AuthN, AuthZ, admission control

Nothing talks directly to etcd except the API server.

Flow:

```
kubectl → kube-apiserver → etcd
```

Controllers and schedulers **also** talk via the API.

---

### 🔹 kube-scheduler (placement engine)

Scheduler does **one thing only**:

> Assigns Pods → Nodes

Process:

1. Watches for Pods without `nodeName`
2. Filters nodes (taints, resources, affinity)
3. Scores remaining nodes
4. Writes binding to API server

Scheduler **does not create Pods**.

---

### 🔹 kube-controller-manager (reconciliation loops)

Runs many controllers:

* Deployment controller
* ReplicaSet controller
* Node controller
* Endpoint controller
* Job controller

Pattern:

```
Observe state → compare desired → act → repeat
```

Controllers:

* Never talk to nodes directly
* Only update objects in API server

---

## 2️⃣ Node internals (where containers actually run)

Each node is just a **Linux machine** with agents.

---

### 🔹 kubelet (node brain)

* Runs on every node
* Watches assigned Pods
* Talks to container runtime
* Reports status back to API server

Responsibilities:

* Start/stop containers
* Mount volumes
* Run probes
* Report health

kubelet ≠ scheduler
kubelet **executes**, scheduler **decides**

---

### 🔹 Container Runtime (CRI)

Examples:

* containerd
* CRI-O

Responsibilities:

* Pull images
* Create containers
* Manage namespaces & cgroups

Kubernetes **does not manage containers directly**.

Flow:

```
kubelet → CRI → container runtime → Linux kernel
```

---

### 🔹 kube-proxy (network rules)

kube-proxy:

* Programs iptables / IPVS
* Implements Services
* Handles virtual IP routing

Important:

* No actual proxying (usually)
* Purely kernel-level rules

---

## 3️⃣ Networking (the hardest part)

Kubernetes networking has **non-negotiable rules**:

1. Every Pod has its own IP
2. Pods can talk directly (no NAT)
3. Nodes can talk to Pods
4. Pods can talk to nodes

Kubernetes **does not implement networking** — CNI does.

---

### 🔹 CNI plugins

Examples:

* Calico
* Cilium
* Flannel

Responsibilities:

* Assign Pod IPs
* Configure routes
* Enforce NetworkPolicies

This is where:

* eBPF
* iptables
* VXLAN
* BGP

live.

---

## 4️⃣ Pod lifecycle (low-level)

A Pod is **not** a container — it’s a **sandbox**.

Steps:

1. Pod object created in API server
2. Scheduler assigns node
3. kubelet notices assignment
4. Sandbox (pause container) created
5. Networking set up
6. Volumes mounted
7. Containers started

If **any step fails**, Pod stays Pending/CrashLoop.

---

## 5️⃣ Controllers > imperative commands

This is critical Kubernetes thinking:

❌ “Create a pod”
✅ “Declare a desired state”

Example:

* Deployment says: replicas = 3
* One Pod dies
* Controller creates a new one
* No human involved

You don’t manage Pods.
You manage **controllers that manage Pods**.

---

## 6️⃣ Why Kubernetes feels “eventually consistent”

Because it **is**.

* API writes are fast
* Controllers react asynchronously
* State converges over time

This is why:

* `kubectl apply` returns immediately
* Pod creation isn’t instant
* Status fields lag behind spec

---

## 7️⃣ Failure modes (real-world)

### Control plane failure

* etcd down → cluster dead
* API server down → no changes possible
* Workloads may continue running

### Node failure

* kubelet stops reporting
* Node marked NotReady
* Pods rescheduled elsewhere

### Partial failure

* Some controllers lag
* Network partitions
* Stale endpoints

Kubernetes assumes **failure is normal**.

---

## 8️⃣ Kubernetes vs Terraform (mental contrast)

| Terraform        | Kubernetes                |
| ---------------- | ------------------------- |
| Imperative apply | Continuous reconciliation |
| External system  | Self-healing system       |
| State file       | API server + etcd         |
| Push-based       | Pull-based                |
| Changes stop     | Changes never stop        |

Kubernetes is always running control loops.

---

## TL;DR (low-level truth)

* Kubernetes is a **distributed state machine**
* API server + etcd are everything
* Controllers reconcile forever
* Nodes just execute instructions
* Networking is delegated
* Failures are assumed, not exceptional


