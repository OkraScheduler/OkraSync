Basic Usage
===========

Prerequisites
-------------

Okra requires Java 8+ and Mongo Java Driver

Usage
-----

* Add Okra to your project as you can see in the :ref:`installation` page.

* Create a class that will store your specific job attributes

.. code-block:: java

    public class CheckJob extends DefaultOkraItem {

        private String someAttribute;

        //getters and setters

    }


* Setup Okra with a MongoDB Driver

.. code-block:: java

    public OkraSync<CheckJob> setupOkra(MongoClient mongo): {
        OkraSync<CheckJob> okra = new OkraSyncBuilder<CheckJob>()
                .withMongo(mongo)
                .withCollection("checkJob")
                .withDatabase(mongoDatabaseName)
                .withItemClass(CheckJob.class)
                .withExpiration(2, TimeUnit.MINUTES)
                .build();
        okra.setup();
        return okra;
    }

* Schedule your job items

.. code-block:: java

    public void schedule(CheckJob item) {

        okra.schedule(item);

    }


* And start processing them...

.. code-block:: java

    public void process() {

        CheckJob item = okra.peek();

        if (item.isPresent()) {
            // process
            okra.reschedule(item)
            // or okra.delete(item);
        }

    }

Enjoy Okra! :)