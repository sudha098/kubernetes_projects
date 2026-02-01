
> **Kubernetes has *no* networking without a CNI plugin.**


![Image](https://www.tigera.io/app/uploads/2021/12/K8s-CNI-diagram01.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2A_DpOTZ4NUm1j8HYUl-uaIg.jpeg)

![Image](https://miro.medium.com/0%2A46-wM_odaA372zTB.png)

# Kubernetes networking **without a CNI plugin**

In **Kubernetes**, networking is **explicitly delegated**.

Kubernetes:

* Defines **rules**
* Does **not implement** networking

Without a CNI plugin, Kubernetes stops at **creating network namespaces** — and that’s it.

---

## 1️⃣ What Kubernetes itself actually does

When a Pod is scheduled to a node, kubelet does **only this**:

1. Creates a **network namespace** for the Pod
2. Starts the **pause container**
3. Passes the netns path to the container runtime

Example (conceptually):

```
/var/run/netns/pod-xyz
```

At this point:

* Pod has a netns
* ❌ No interfaces
* ❌ No IP
* ❌ No routes
* ❌ No connectivity

---

## 2️⃣ What happens next (normally)

Normally:

```
kubelet → CNI plugin → Linux networking
```

But **without CNI**, this step never happens.

So the Pod is stuck in:

```
ContainerCreating
```

Or:

```
NetworkPluginNotReady
```

---

## 3️⃣ What the Pod netns looks like without CNI

Inside the Pod netns:

```bash
ip addr
```

You see:

```
lo: LOOPBACK
```

That’s it.

No:

* `eth0`
* Pod IP
* Default route

Even `localhost` networking works **only inside the same container**, not across containers or Pods.

---

## 4️⃣ What Kubernetes networking rules require (but don’t enforce)

Kubernetes mandates:

1. Every Pod has a unique IP
2. Pods can reach each other directly
3. No NAT between Pods
4. Node ↔ Pod connectivity

But Kubernetes:

* Never creates veth pairs
* Never assigns IPs
* Never programs routes
* Never touches iptables for Pod networking

That’s **100% CNI territory**.

---

## 5️⃣ Why Kubernetes refuses to “half-work” without CNI

Design choice:

> **Fail fast instead of silently broken networking**

Reasons:

* Inconsistent networking = impossible debugging
* Controllers assume connectivity
* kube-proxy needs Pod IPs
* DNS depends on Pod networking

So Kubernetes:

* Creates netns
* Waits
* Refuses to start containers

---

## 6️⃣ What *would* be required to make it work manually

If you wanted to replace CNI yourself, you’d need to:

### Per Pod:

1. Create a veth pair
2. Move one end into the Pod netns
3. Assign an IP
4. Set default route
5. Connect host end to:

   * bridge, or
   * routing table
6. Ensure Pod IP is routable cluster-wide

### Per Node:

* IPAM
* Route propagation
* ARP handling
* MTU management

This is literally **re-implementing a CNI plugin**.

---

## 7️⃣ Why Docker “worked” without CNI (historical confusion)

Docker:

* Created `docker0` bridge
* Did NAT
* Hid complexity

Kubernetes:

* Refused that model
* Required flat networking
* Forced pluggability

This is why Kubernetes networking feels harder — but scales better.

---

## 8️⃣ kube-proxy without CNI (also broken)

kube-proxy:

* Programs rules **assuming Pod IPs exist**
* Needs working netns + interfaces

Without CNI:

* Services don’t work
* ClusterIP is meaningless
* Endpoints unreachable

---

## 9️⃣ The minimal truth table

| Component  | Without CNI  |
| ---------- | ------------ |
| Pod netns  | ✅ created    |
| Pod IP     | ❌            |
| veth       | ❌            |
| Routing    | ❌            |
| Pod ↔ Pod  | ❌            |
| Node ↔ Pod | ❌            |
| Services   | ❌            |
| DNS        | ❌            |
| Cluster    | ❌ functional |

---

## 10️⃣ Why CNI is *the* Kubernetes complexity

CNI must solve:

* Linux networking
* IPAM
* Routing
* Policy
* Performance
* Observability

Kubernetes intentionally offloaded this because:

> There is no “one correct” networking solution.

---

## TL;DR (brutal truth)

* Kubernetes **creates netns**
* Stops
* Waits for CNI
* Without CNI → **no cluster**

CNI is **not optional plumbing**.
It *is* Kubernetes networking.

