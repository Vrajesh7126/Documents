Spring rollback for a RunTime Exception, not for Checked Exception.

Spring skips the AOP when a function with an annotation `@Transactional` is called from within the same class.

# Transaction Propagation

What happens when one transactional method calls another transactional method?

| Propagation       | Existing TX (Outer has `@Transactional`) | No Existing TX (Outer has no `@Transactional`) |
| ----------------- | ---------------------------------------- | ---------------------------------------------- |
| **REQUIRED**      | Join existing TX                         | Create new TX                                  |
| **REQUIRES_NEW**  | Suspend existing TX, create new TX       | Create new TX                                  |
| **SUPPORTS**      | Join existing TX                         | Run without TX                                 |
| **NOT_SUPPORTED** | Suspend existing TX, run without TX      | Run without TX                                 |
| **MANDATORY**     | Join existing TX                         | ❌ Exception                                    |
| **NEVER**         | ❌ Exception                              | Run normally                                   |
| **NESTED**        | Create savepoint inside existing TX      | Create new TX                                  |

