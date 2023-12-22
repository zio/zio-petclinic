# ZIO Pet Clinic: An idiomatic pet clinic application written with ZIO.

This is a fullstack web app that serves as an example for best utilizing ZIO and
the libraries within its ecosystem.

This app uses:

- [ZIO HTTP](https://github.com/dream11/zio-http) for the HTTP server
- [ZIO JSON](https://github.com/zio/zio-json) for JSON serialization
- [ZIO Quill](https://github.com/zio/zio-quill) for SQL queries
- [ZIO Test Containers](https://github.com/scottweaver/testcontainers-for-zio) for testing database queries

## Getting Started

You can play with the app [here](https://zio-pet-clinic.surge.sh). *Keep in mind that this
runs on a free Heroku instance, so it might take ~10 seconds to respond the first time. Also,
all data will be reset every 15 minutes in order to keep it nice and tidy.*

You can also run the app locally. First, open your terminal and clone the project.

```shell
git clone git@github.com:zio/zio-petclinic.git
cd zio-petclinic
```

Next, open three terminal windows or panes and run the following commands:

**Pane 1**
*Installs frontend dependencies and runs the development server*
```shell
yarn install
yarn exec vite
```

**Pane 2**
*Compiles and runs the http server*
```shell
sbt
~ backend/reStart
```

**Pane 3**
*Compiles the frontend JavaScript*
```shell
sbt
~ frontend/fastLinkJS
```

Then open the browser and navigate to the address shown by vite, usually [localhost:5173](http://localhost:5173).
