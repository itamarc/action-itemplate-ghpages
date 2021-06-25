To re-create the tag used to test the action, both locally and remote:

```bash
# ~/devel/action-itemplate-ghpages (master)
$ git tag -d v1test
Deleted tag 'v1test' (was 752d447)

# ~/devel/action-itemplate-ghpages (master)
$ git push origin --delete v1test
To https://github.com/itamarc/action-itemplate-ghpages.git
 - [deleted]         v1test

# ~/devel/action-itemplate-ghpages (master)
$ git tag v1test

# ~/devel/action-itemplate-ghpages (master)
$ git push origin --tags
Total 0 (delta 0), reused 0 (delta 0), pack-reused 0
To https://github.com/itamarc/action-itemplate-ghpages.git
 * [new tag]         v1test -> v1test
```
