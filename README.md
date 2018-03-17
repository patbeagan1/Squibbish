```
pbeagan@Patricks-MacBook-Pro:~/IdeaProjects/squibbish/out/artifacts/main_jar 14:21:31 (master) 6 $ cat sq.txt
fn all_the_tests = { input |

  echo hello $input\!
  fn hello_world ={ a | echo hello world }


  fn printAndDo = { a |
    echo $a
    do $a
  }
  do printAndDo hello_world

  fn count_toten = {|
    for 0..10 {
      echo $it
    }
  }

  fn count_toten_by_two = {|
    for 0..10..2 {
      echo $it
    }
  }
  do count_toten_by_two

  fn count_toten_by_two_with_varname = {|
    for 0..10..2 each {
      echo $each
    }
  }
  do count_toten_by_two_with_varname

  fn sayhelloifone = { argument |
    branch argument {
      1 { echo hello }
      * {
        echo didnt understand the argument.
        fn sayhelloiftwo = { argument |
          br argument {
            2 { echo hello }
            * { echo Dont understand the argument. }
          }
        }
        do sayhelloiftwo "$argument"
        do sayhelloiftwo "1"
        do sayhelloiftwo "2"
        echo this is from inside of the loop.
        echo yes // This is a comment.
        for 1..5 {
          echo hello$it
        }
      }
    }
  }

  fn testing_let = {|
    let a = 10
    echo $a

  }

  do testing_let; echo; do sayhelloifone 2
  echo
  do sayhelloifone 1
  do count_toten 1
  echo
  do hello_world
}
fn myfunction = { a |
    for 1..5 {
        br { $a = $it  {
                echo $a
             } 1 {
        echo false $it
             }
        }
    }
}
pbeagan@Patricks-MacBook-Pro:~/IdeaProjects/squibbish/out/artifacts/main_jar 14:18:03 (master) 6 $ cat out.sh
 all_the_tests () { local input="${1}"; echo hello $input\!; hello_world () { local a="${1}"; echo hello world; }; printAndDo () { local a="${1}"; echo $a; $a; }; printAndDo hello_world; count_toten () { for it in `seq 0 10`; do echo $it; done; }; count_toten_by_two () { for it in `seq 0 2 10`; do echo $it; done; }; count_toten_by_two; count_toten_by_two_with_varname () { for each in `seq 0 2 10`; do echo $each; done; }; count_toten_by_two_with_varname; sayhelloifone () { local argument="${1}"; case "${argument}" in 1 ) echo hello;  ;; * ) echo didnt understand the argument.; sayhelloiftwo () { local argument="${1}"; case "${argument}" in 2 ) echo hello;  ;; * ) echo Dont understand the argument.;  ;; esac; }; sayhelloiftwo "$argument"; sayhelloiftwo "1"; sayhelloiftwo "2"; echo this is from inside of the loop.; echo yes // This is a comment.; for it in `seq 1 5`; do echo hello$it; done;  ;; esac; }; testing_let () { a=10; echo $a; }; testing_let; echo; sayhelloifone 2; echo; sayhelloifone 1; count_toten 1; echo; hello_world; }; myfunction () { local a="${1}"; for it in `seq 1 5`; do if [ $a = $it ]; then echo $a; elif [ 1 ]; then echo false $it; fi; done; }; pbeagan@Patricks-MacBook-Pro:~/IdeaProjects/squibbish/out/artifacts/main_jar 14:18:15 (master) 6 $ . out.sh
pbeagan@Patricks-MacBook-Pro:~/IdeaProjects/squibbish/out/artifacts/main_jar 14:18:20 (master) 6 $ type all_the_tests
all_the_tests is a function
all_the_tests ()
{
    local input="${1}";
    echo hello $input\!;
    function hello_world ()
    {
        local a="${1}";
        echo hello world
    };
    function printAndDo ()
    {
        local a="${1}";
        echo $a;
        $a
    };
    printAndDo hello_world;
    function count_toten ()
    {
        for it in `seq 0 10`;
        do
            echo $it;
        done
    };
    function count_toten_by_two ()
    {
        for it in `seq 0 2 10`;
        do
            echo $it;
        done
    };
    count_toten_by_two;
    function count_toten_by_two_with_varname ()
    {
        for each in `seq 0 2 10`;
        do
            echo $each;
        done
    };
    count_toten_by_two_with_varname;
    function sayhelloifone ()
    {
        local argument="${1}";
        case "${argument}" in
            1)
                echo hello
            ;;
            *)
                echo didnt understand the argument.;
                function sayhelloiftwo ()
                {
                    local argument="${1}";
                    case "${argument}" in
                        2)
                            echo hello
                        ;;
                        *)
                            echo Dont understand the argument.
                        ;;
                    esac
                };
                sayhelloiftwo "$argument";
                sayhelloiftwo "1";
                sayhelloiftwo "2";
                echo this is from inside of the loop.;
                echo yes // This is a comment.;
                for it in `seq 1 5`;
                do
                    echo hello$it;
                done
            ;;
        esac
    };
    function testing_let ()
    {
        a=10;
        echo $a
    };
    testing_let;
    echo;
    sayhelloifone 2;
    echo;
    sayhelloifone 1;
    count_toten 1;
    echo;
    hello_world
}
pbeagan@Patricks-MacBook-Pro:~/IdeaProjects/squibbish/out/artifacts/main_jar 14:18:34 (master) 6 $ all_the_tests
hello !
hello_world
hello world
0
2
4
6
8
10
0
2
4
6
8
10
10

didnt understand the argument.
hello
Dont understand the argument.
hello
this is from inside of the loop.
yes // This is a comment.
hello1
hello2
hello3
hello4
hello5

hello
0
1
2
3
4
5
6
7
8
9
10

hello world
pbeagan@Patricks-MacBook-Pro:~/IdeaProjects/squibbish/out/artifacts/main_jar 14:18:54 (master) 6 $
```
