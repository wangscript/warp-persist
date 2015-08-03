## Automated transaction management ##

Apart from the dependency injection of data services, perhaps the most compelling reason to use warp-persist (and indeed Google Guice) is the automatic management of database transactions.

Many architectures are replete with anti-patterns that arise purely from workarounds dealing with transaction and recovery code. Warp-persist removes these concerns from your purview by providing automated transaction management behind-the-scenes.

Not only does this avoid repetitive, error-prone transaction logic in your classes, but it also provides a simple declarative model to wrapping your business logic inside transactions. With straightforward recovery and isolation mechanisms.