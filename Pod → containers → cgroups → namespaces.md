
![Image](https://www.researchgate.net/publication/355682399/figure/fig3/AS%3A1086139502989331%401635967245804/Namespaces-in-Kubernetes.ppm)

![Image](https://media.licdn.com/dms/image/v2/D4D12AQFN8YgF9I_8uQ/article-cover_image-shrink_720_1280/article-cover_image-shrink_720_1280/0/1704459242333?e=2147483647\&t=HzDiWXAkhury84Rofp2ZpdgtDpgFxUuIarVLJTNCZs4\&v=beta)

![Image](https://miro.medium.com/1%2AoQBStcYmbbtP5n58I1Lb_A.png)

# Pod → cgroups → namespaces (deep mapping)

At runtime, **Kubernetes** Pods are **not special**.
They’re just **Linux processes grouped by kernel primitives**.

---

## The core idea (one sentence)

> A **Pod** is a group of processes that **share Linux namespaces** and are **accounted by Linux cgroups**.

Everything else is abstraction.

---

# 1️⃣ Pod → processes

A Pod contains:

* 1 **pause container** (sandbox)
* N **application containers**

On the node, this becomes:

```
pause (PID X)
├── app-container-1 (PID Y)
├── app-container-2 (PID Z)
```

All of these are **normal Linux processes**.

---

## Why the pause container exists

The pause container:

* Is the **first process**
* Owns the **shared namespaces**
* Never exits (unless Pod dies)

Other containers:

* **Join** the pause container’s namespaces

If pause dies → Pod dies.

---

# 2️⃣ Namespaces (isolation)

Namespaces answer:

> “What can this process *see*?”

A **Pod shares namespaces** across containers.

---

## Namespace mapping per Pod

| Namespace | Scope           | Shared across Pod?                 |
| --------- | --------------- | ---------------------------------- |
| `net`     | Networking      | ✅ YES                              |
| `ipc`     | Shared memory   | ✅ YES                              |
| `uts`     | Hostname        | ✅ YES                              |
| `pid`     | Process tree    | ❌ (usually isolated per container) |
| `mnt`     | Filesystem      | ❌                                  |
| `user`    | UID/GID mapping | ❌                                  |

### Key insight

👉 **Networking is Pod-scoped**, not container-scoped.

That’s why:

* Containers in a Pod share IP
* `localhost` works between containers
* One port per Pod, not per container

---

## What “share net namespace” really means

Inside the Pod:

```bash
ip addr
```

You see:

* One Pod IP
* One network stack
* Shared routing table

Containers **do not have individual IPs**.

---

# 3️⃣ cgroups (resource control)

cgroups answer:

> “How much CPU/memory can this process use?”

Pods are **resource boundaries**.

---

## cgroup hierarchy (simplified)

```
/sys/fs/cgroup/
└── kubepods.slice
    └── kubepods-burstable.slice
        └── kubepods-burstable-podUID.slice
            ├── container1.scope
            ├── container2.scope
            └── pause.scope
```

Important points:

* Pod has its **own cgroup**
* Containers are **children**
* Limits apply hierarchically

---

## Resource enforcement flow

Example:

```yaml
resources:
  limits:
    memory: 512Mi
```

Kernel behavior:

1. Pod cgroup limit = 512Mi
2. Containers consume memory
3. If total > 512Mi
4. **OOM killer fires**
5. One container dies
6. kubelet restarts it

Kubernetes does **not kill containers** — the **kernel does**.

---

# 4️⃣ CPU vs memory (critical difference)

### CPU

* Throttled
* Fair scheduling
* No hard kill

### Memory

* Hard limit
* OOM killer
* Instant death

This is why memory limits are dangerous if mis-sized.

---

# 5️⃣ Process visibility (PID namespace)

Containers usually:

* Have their **own PID namespace**
* Cannot see processes in sibling containers

Unless:

```yaml
shareProcessNamespace: true
```

Then:

* Containers can `ps aux` each other
* Debug sidecars work
* Security risk increases

---

# 6️⃣ Filesystems (mount namespaces)

Each container has:

* Its own root filesystem
* Image layers
* Writable layer

Shared via:

* Volumes (`emptyDir`, `configMap`, etc.)

Pod ≠ shared filesystem by default.

---

# 7️⃣ Security context → kernel mapping

Example:

```yaml
securityContext:
  runAsUser: 1000
  readOnlyRootFilesystem: true
```

Kernel effects:

* User namespace / UID mapping
* Mount flags set read-only
* Syscall filtering (seccomp)
* Capability drops

Kubernetes **configures**, kernel **enforces**.

---

# 8️⃣ Complete mental model

```
Pod
├── Linux net namespace
├── Linux ipc namespace
├── Linux uts namespace
├── Pod cgroup (cpu + memory)
│   ├── pause process
│   ├── container A
│   └── container B
└── Volumes (shared mounts)
```

Kubernetes does:

* Object model
* Reconciliation
* API

Linux does:

* Isolation
* Scheduling
* Enforcement

---

# 9️⃣ Why this matters in real life

Understanding this explains:

✔ Why one container OOM kills the whole Pod
✔ Why Pods are the smallest schedulable unit
✔ Why sidecars work
✔ Why `localhost` works
✔ Why limits feel “weird”
✔ Why debugging Kubernetes means debugging Linux

Kubernetes is **policy + control loops**.
Linux is **execution + enforcement**.

---

## TL;DR

* Pod = **shared namespaces**
* Containers = **processes**
* cgroups = **resource limits**
* Kernel = **final authority**
* Kubernetes = **orchestrator, not executor**

