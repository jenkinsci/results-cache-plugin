Results Cache Plugin
====================
This Jenkins(TM) Plugin allows to avoid the execution of the same job with the same parameters more than once in order
to save resources and time.
To identify every job it uses a hash identifier which is composed by the jenkins master url, the full job name (including folder) and
the selected parameters with their values (selected in the job configuration).
It works using a complementary external cache server which has to implement the following API:

Cache Server API
----------------
* GET /job-results/{hash}: Returns a cached result from its job hash. Returns 'NOT_BUILT' if the job hash is not found.
* POST /job-results/{hash}/{result}: Adds a new result in the cache for the provided job hash
* DELETE /job-results/clear: Removes all the cache values

You can find a reference implementation in [results cache service](https://github.com/king/results-cache-service) or implement it on your own.

Latest release
--------------
1.1.1

How to build
------------
```mvn hpi:hpi```

How to use
----------
1. Install the plugin in your Jenkins master using the *hpi* file
2. Go to *Jenkins(TM) -> configuration* section and locate **Results Cache Plugin** section
3. Add the URL to the *results cache service* and a *timeout* for every call ![Plugin Config](./docs/plugin-config.png)
4. Activate the plugin in your jobs in the **Build Environment** section ![Jon Config](./docs/job-config.png)

Job DSL configuration
---------------------
You can enable the results cache plugin in your jenkins jobs using Job DSL. You only need to configure it in the `wrappers` section of your dsl.

```
resultsCache(boolean excludeMachineName, String hashableProperties)
```

Example:
```
wrappers {
    resultsCache(true, 'PARAM_1, PARAM_2')
}
```


###### Note: like above if you provide an empty list that means all job parameters will be used.

Contact us!
------------

The original King authors of this code:

 - Francisco Javier García Orduña (francisco.orduna@king.com)
 - David Campos Valls (david.campos@king.com)

With any questions or suggestions feel free to reach out to us in email anytime!
