<context:annotation-config> is used to activate annotations in beans already registered in the application context (no matter if they were defined with XML or by package scanning).

<context:component-scan> can also do what <context:annotation-config> does but <context:component-scan> also scans packages to find and register beans within the application context.