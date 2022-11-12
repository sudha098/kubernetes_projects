
PROCESS OF ENCRYPTING DATA AT REST.
------------------------------------

kubectl create secret generic my-secret --from-literal=key1=supersecret --dry-run=client -o yaml > secret.yaml

kubectl apply -f secret.yaml

yum install etcd-client -y

##########determining whether encryption at rest is already enabled
ETCDCTL_API=3 etcdctl \
   --cacert=/etc/kubernetes/pki/etcd/ca.crt   \
   --cert=/etc/kubernetes/pki/etcd/server.crt \
   --key=/etc/kubernetes/pki/etcd/server.key  \
   get /registry/secrets/default/my-secret | hexdump -C

ps aux | grep kube-apiserver | grep "--encryption-provider-config"

ls /etc/kubernetes/manifests/

cat /etc/kubernetes/manifests/kube-apiserver.yaml


##########Encrypting your data

#Generate a 32-byte random key and base64 encode it.

head -c 32 /dev/urandom | base64

kubectl apply -f enc.yaml

mkdir /etc/kubernetes/enc

mv encryption-config.yaml /etc/kubernetes/enc

ls /etc/kubernetes/enc

vim /etc/kubernetes/manifests/kube-apiserver.yaml

#spec:
   containers:
   - command:
     - kube-apiserver
     ...
     - --encryption-provider-config=/etc/kubernetes/enc/enc.yaml  # <-- add this line
     volumeMounts:
     ...
     - name: enc                           # <-- add this line
       mountPath: /etc/kubernetes/enc      # <-- add this line
       readonly: true                      # <-- add this line
     ...
   volumes:
   ...
   - name: enc                             # <-- add this line
     hostPath:                             # <-- add this line
       path: /etc/kubernetes/enc           # <-- add this line
       type: DirectoryOrCreate             # <-- add this line

kubectl get pods

crictl pods


##########Verifying that data is encrypted
-----
ps aux | grep kube-apiserver | grep "--encryption-provider-config"

kubectl create secret generic my-secret-2 --from-literal=key2=topsecret --dry-run=client -o yaml > secret2.yaml

kubectl get pods

ETCDCTL_API=3 etcdctl \
   --cacert=/etc/kubernetes/pki/etcd/ca.crt   \
   --cert=/etc/kubernetes/pki/etcd/server.crt \
   --key=/etc/kubernetes/pki/etcd/server.key  \
   get /registry/secrets/default/my-secret | hexdump -C


#Ensure all Secrets are encrypted

kubectl get secrets --all-namespaces -o json | kubectl replace -f -


##########To disable encryption at rest, place the identity provider as the first entry in the config and restart all kube-apiserver processes.

Secrets are not encrypted, so it is not safer in that sense. However, some best practices around using secrets make it safer. As in best practices like:

a. Not checking-in secret object definition files to source code repositories.

b. Enabling Encryption at Rest for Secrets so they are stored encrypted in ETCD.

Please refer: https://kubernetes.io/docs/tasks/administer-cluster/encrypt-data/ for more understanding.
----------------------------------









