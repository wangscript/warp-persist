# Introduction #

TODO

# Best Practices #
If for some reason you can't use Dynamic Accessors, we highly recommend that you throw an Exception in the bodies of your DF methods. This will help detect methods that for some reason did not get proxied by Warp Persist:
  * You forgot to specify a unit annotation: `@Finder(unit=...)`
  * You misplaced annotations
  * ...

**Don't** do this:
```
@Transactional(...)
@Finder(...)
public List<Product> findAllProducts() {
    return null;
}
```


Instead, prefer this:
```
@Transactional(...)
@Finder(...)
public List<Product> findAllProducts() {
    throw new AssertionError();
}
```

Or ideally, use Dynamic Accessors:
```
public interface ProductAccess {
    @Transactional(...)
    @Finder(...)
    public List<Product> findAllProducts();
}
```