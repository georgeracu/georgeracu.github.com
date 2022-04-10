
# Docker

<hr />

Docker is a `command-line program`, a `background daemon`, and `a set of remote services` that take a logistical approach to solving common software problems and simplifying your experience installing, running, publishing, and removing software. It accomplishes this using a UNIX technology called containers.

`Docker doesn’t provide the container technology, but it specifically makes it simpler to use.`

---

# Containers

The containers that Docker builds are isolated with respect to eight aspects:

- PID namespace—Process identifiers and capabilities
- UTS namespace—Host and domain name
- MNT namespace—File system access and structure
  IPC namespace—Process communication over shared memory
- NET namespace—Network access and structure
- USR namespace—User names and identifiers
- _chroot()_—Controls the location of the file system root
- _cgroups_—Resource protection

---

# Running Docker

<hr />

.fs-6[
- Docker runs as _root_. Instead of using _sudo_ you might need to create a user group _docker_ setting that group as the owner of the docker socket, and adding your user to that group.
- `--detach` _(-d)_ run the Docker process in the background (creates a _daemon_).
- `--interactive` _(-i)_ keep the _standard input stream (stdin)_ openfor the container.
- `--tty` _(-t)_ allocate a virtual terminal to the container.
- `docker ps` will display information about each running container.
- `docker restart <container_name>` will restart the container specified.
- `docker logs <container_name>` will display the logs for the container specified. The log is never rotated or truncated, so the data written to the log for a container will remain and grow as long as the container exists.
- `docker logs <container_name> --follow` _(-f)_ to follow the logs. End with _Ctrl+C_.
- `docker exec` allows to run additional processes in a running container.
- `docker run -d --name webA nginx:latest` and `docker run -d --name webB nginx:latest` will create two containers from the same image.
- `docker rename <old_name> <new_name>` will rename a container.
- `CID=$(docker create nginx:latest)` start the container in a _stopped_ state. It will assign the output id to the _\$CID_ variable.
- `--cidfile <file_name>` will write the container id to the file specified.
]

---

# Dockerfile

<hr />

A Dockerfile is a `file` that contains `instructions` for building an image. The instructions are followed by the Docker image builder from top to bottom and can be used to change anything about an image.

.fs-6[
- Docker provides an automated image builder that reads instructions from Dockerfiles.
- Each Dockerfile instruction results in the creation of a single image layer.
  Merge instructions whenever possible to minimize the size of images and layer count.
- Dockerfiles include instructions to set image metadata like the default user, exposed ports, default command, and entrypoint.
- Other Dockerfile instructions copy files from the local file system or remote location into the produced images.
- Downstream builds inherit build triggers that are set with ONBUILD instructions in an upstream Dockerfile.
- Startup scripts should be used to validate the execution context of a container before launching the primary application.
- A valid execution context should have appropriate environment variables set, network dependencies available, and an appropriate user configuration.
- Init programs can be used to launch multiple processes, monitor those processes, reap orphaned child processes, and forward signals to child processes.
- Images should be hardened by building from content addressable image identifiers, creating a non-root default user, and disabling or removing any executable with SUID or SGID permissions.
]

---

# Docker Compose

<hr />

Compose is a tool for defining, launching, and managing services, where a _service_ is defined as one or more replicas of a Docker container. Services and systems of services are defined in [YAML files](http://yaml.org) and managed with the command-line program _docker-compose_. With Compose you can use simple commands to accomplish these tasks:

- Build Docker images
- Launch containerized applications as services
- Launch full systems of services
- Manage the state of individual services in a system
- Scale services up or down
- View logs for the collection of containers making a service

---

# Docker Commands

<hr />

- Find the id for the Docker container from host EC2

```bash
sudo docker ps
```

- Inspect the container and get the DB env vars

```bash
sudo docker inspect <container_id> | grep DB
```

- Install `PSQL` on Ubuntu host

```bash
sudo apt-get install postgresql-client-common
sudo apt-get install postgresql-client-9.6
```

- Run psql with host, username and database

```bash
psql -h <host_name>  -U <username> -d <database>
```

---

# More Docker commands

<hr />

- Connect to the Docker container

```bash
sudo docker exec -ti <container_id> bash
```

- View latest `n` commits with messages

```bash
git log --pretty=format:"%h %s" HEAD~n..HEAD
```

- Git rebase last `n` commits

```bash
git rebase -i HEAD~n
# use `pick` to select which commit to keep and `squash` which one to be squashed
# on message selection page delete unwanted lines and keep the messages you want
git push -f origin <name_of_branch_here>
```
