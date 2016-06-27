WowAPP test (Drones)
====================

Usage:
1. Build jar via `./gradlew jar`. The jar `wowapptest.jar` would be placed in project root dir.
2. Run jar via `java -jar wowapptest.jar`. It will produce output to console. Output should look like this:
```
2016-06-24 15:43:05,236 [Thread-1] INFO  wowapptest.Dispatcher  - Registered traffic at station Bank from drone 5937, time:2016-01-01 00:00:00.002, speed:300.00, traffic:HEAVY
2016-06-24 15:43:05,236 [main] INFO  wowapptest.Dispatcher  - Drone 6043 position 51.503620, -0.019870 - waiting
2016-06-24 15:43:05,236 [Thread-0] INFO  wowapptest.Drone  - Drone 6043 started moving to 51.558470, -0.105610
2016-06-24 15:43:05,238 [main] INFO  wowapptest.Dispatcher  - Drone 5937 position 51.513405, -0.089058 - waiting
2016-06-24 15:43:05,239 [Thread-0] INFO  wowapptest.Dispatcher  - Registered traffic at station Canary Wharf from drone 6043, time:2016-01-01 00:00:00.009, speed:300.00, traffic:HEAVY
2016-06-24 15:43:10,240 [main] INFO  wowapptest.Dispatcher  - Drone 6043 position 51.506040, -0.023653 - moving (4.41%) to 51.558470, -0.105610
2016-06-24 15:43:10,241 [main] INFO  wowapptest.Dispatcher  - Drone 5937 position 51.513405, -0.089058 - waiting
2016-06-24 15:43:15,243 [main] INFO  wowapptest.Dispatcher  - Drone 6043 position 51.508728, -0.027855 - moving (9.32%) to 51.558470, -0.105610
2016-06-24 15:43:15,244 [Thread-1] INFO  wowapptest.Drone  - Drone 5937 started moving to 51.503620, -0.019870
2016-06-24 15:43:15,244 [main] INFO  wowapptest.Dispatcher  - Drone 5937 position 51.513405, -0.089058 - waiting
2016-06-24 15:43:20,246 [main] INFO  wowapptest.Dispatcher  - Drone 6043 position 51.511416, -0.032057 - moving (14.22%) to 51.558470, -0.105610
2016-06-24 15:43:20,248 [main] INFO  wowapptest.Dispatcher  - Drone 5937 position 51.512657, -0.083771 - moving (7.64%) to 51.503620, -0.019870
2016-06-24 15:43:25,250 [main] INFO  wowapptest.Dispatcher  - Drone 6043 position 51.514105, -0.036259 - moving (19.12%) to 51.558470, -0.105610
...
2016-06-24 15:46:55,372 [main] INFO  wowapptest.Dispatcher  - Drone 5937 position 51.536751, -0.079169 - moving (57.92%) to 51.558470, -0.105610
2016-06-24 15:47:00,373 [main] INFO  wowapptest.Dispatcher  - Drone 6043 position 51.513753, -0.043561 - moving (69.55%) to 51.503620, -0.019870
2016-06-24 15:47:00,373 [main] INFO  wowapptest.Dispatcher  - Drone 5937 position 51.539740, -0.082808 - moving (63.13%) to 51.558470, -0.105610
2016-06-24 15:47:05,375 [main] INFO  wowapptest.Dispatcher  - Drone 6043 position 51.511629, -0.038596 - moving (75.93%) to 51.503620, -0.019870
2016-06-24 15:47:05,375 [Thread-0] INFO  wowapptest.Drone  - Drone 6043 is shutting down
2016-06-24 15:47:05,376 [Thread-1] INFO  wowapptest.Drone  - Drone 5937 is shutting down
2016-06-24 15:47:05,377 [main] INFO  wowapptest.Dispatcher  - Drone 5937 position 51.542430, -0.086083 - moving (68.92%) to 51.558470, -0.105610
2016-06-24 15:47:10,379 [main] INFO  wowapptest.Dispatcher  - Drone 6043 position 51.511629, -0.038596 - moving (75.93%) to 51.503620, -0.019870
2016-06-24 15:47:10,380 [main] INFO  wowapptest.Dispatcher  - Drone 5937 position 51.542430, -0.086083 - moving (68.92%) to 51.558470, -0.105610
```
3. Wait about 5 minutes for completion (each drone will receive shutdown cmd).

## CSV-files
Project contains following csv files:
- stations.csv - list of tube stations with coordinates
- drones.csv - list of drones with initial coordinates
- commands.csv - list of commands and times

Those files are read at startup.

## Notes
- Drones runs in real-time, however time is adjusted to the time of first command during Dispatcher startup.
- No 10 cmd requirement implemented. This is JVM program. I see no reason for that limit.
- No unit tests implemented. The time for the assignment was limited. If it's required to see my unit tests, please
  see my github account - https://github.com/dmitrypekar?tab=activity kafka-mesos project contains a lot of unit-tests.
- Added idea file for convenience