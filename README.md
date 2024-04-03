# CSCI3170

CSCI 3170 Project

## Quick Start

Before running the project, be sure to create a file with the name `credentials` under the `./src` directory. This file should contain two lines, with the first being your username and the second being your password for the provided database from CSE.

_Do note that connection to CSE vpn is required in order to connect to the databases._

### Without Docker

Run the following:

```
cd src
make
make run
```

### With Docker

Run the following:

```
sh build-docker.sh
sh run-docker.sh
```
