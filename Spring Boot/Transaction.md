| Propagation       | Existing TX (Outer has `@Transactional`) | No Existing TX (Outer has no `@Transactional`) |
| ----------------- | ---------------------------------------- | ---------------------------------------------- |
| **REQUIRED**      | Join existing TX                         | Create new TX                                  |
| **REQUIRES_NEW**  | Suspend existing TX, create new TX       | Create new TX                                  |
| **SUPPORTS**      | Join existing TX                         | Run without TX                                 |
| **NOT_SUPPORTED** | Suspend existing TX, run without TX      | Run without TX                                 |
| **MANDATORY**     | Join existing TX                         | ❌ Exception                                    |
| **NEVER**         | ❌ Exception                              | Run normally                                   |
| **NESTED**        | Create savepoint inside existing TX      | Create new TX                                  |
