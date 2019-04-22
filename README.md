# Executor Benchmark

This project is used to generically benchmark potential different implementations of the Java ExecutorService, gathering various metrics and comparisons, as well as testing on various different profiles.

## TODO

- Start implementing calculation of various metrics in the profile builder, such as:
  - Request completion times
  - Number of threads used
- Create a benchmark runner to easily coallate reports on multiple profiles vs. multiple executor services
  - Pretty printing
  - Using same random seeds
  - Doing multiple attempts per profile per service.