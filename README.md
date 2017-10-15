# clj-wiremock
[![Build Status](https://travis-ci.org/kelveden/clj-wiremock.svg?branch=master)](https://travis-ci.org/kelveden/clj-wiremock)

Clojure bindings for [WireMock](http://wiremock.org/)

## Usage
![](https://clojars.org/kelveden/clj-wiremock/latest-version.svg)

### As a fixture
Typically you'll want wiremock to fire up at the beginning of your
test namespaces and shutdown at the end. To achieve this, `wiremock-fixture`
is provided as your "once" test fixture.

(Note that `with-stubs` automatically clears down wiremock of all
stubs at the end of the block.)

```clj
(defn around-all
  [f]
  (wmk/wiremock-fixture {:port wiremock-port} f))

(use-fixtures :once around-all)

(deftest can-ping
  (wmk/with-stubs
      [{:req [:GET "/ping"] :res [200 {:body "pong"}]}]
  
      (let [{:keys [status]} (http/get (wmk/url "/ping"))]
        (is (= 200 status)))))
```

See the [full code](test/clj_wiremock/test/examples/as_fixture.clj). 

### As a block
Sometimes you only need wiremock in one or two tests rather than the entire
suite. For cases like this `with-wiremock` is provided to create 
code blocks in which wiremock has been fired up. Wiremock will be stopped
at the end of the block.

```clj
(wmk/with-wiremock {:port wiremock-port}
    (wmk/with-stubs
      [{:req [:GET "/ping"] :res [200 {:body "pong"}]}]

      (let [{:keys [status]} (http/get (wmk/url "/ping"))]
        (is (= 200 status)))))
```

See the [full code](test/clj_wiremock/test/examples/as_block.clj). 


### Manually
If you find yourself wanting to manipulate wiremock from your REPL
it might be best to manipulate the wiremock server directly. Below is a
rather contrived test to illustrate this.

Note that because we're calling `server/stub!` directly the stubs will not get
cleared down automatically - i.e. if you're not
using `with-stubs` then you must handle clearing down stubs manually. (Of course,
in the example below all this is a moot point as the server is being shut down in the `finally`
block anyway.)

```clj
(deftest can-ping
  (let [wiremock (server/init-wiremock {:port wiremock-port})]
    (try
      (server/start! wiremock)
      (server/register-stub! wiremock {:req [:GET "/ping"] :res [200 {:body "pong"}]})

      (let [{:keys [status]} (http/get (server/url wiremock "/ping"))]
        (is (= 200 status)))

      (finally (server/stop! wiremock)))))
```

See the [full code](test/clj_wiremock/test/examples/manually.clj).

### Via Java interop
Sometimes the helper functions provided aren't enough and you need to get your hands
dirty with the underlying Java WireMockServer.

```clj
(deftest can-ping
  (let [wiremock (server/init-wiremock {:port wiremock-port})
        wmk-java (.wmk-java wiremock)]
    (try
      (.start wmk-java)

      (http/post (str "http://localhost:" (.port wmk-java) "/__admin/mappings/new")
                 {:body (json/generate-string {:request  {:method :GET :url "/ping"}
                                               :response {:status 200 :body "pong"}})})

      (let [{:keys [status]} (http/get (str "http://localhost:" (.port wmk-java) "/ping"))]
        (is (= 200 status)))

      (finally (.stop wmk-java)))))
```

See the [full code](test/clj_wiremock/test/examples/with_java_interop.clj).