 all_the_tests () { local input="${1}"; echo hello $input\!; hello_world () { local a="${1}"; echo hello world; }; printAndDo () { local a="${1}"; echo $a; $a; }; printAndDo hello_world; count_toten () { for it in `seq 0 10`; do echo $it; done; }; count_toten_by_two () { for it in `seq 0 2 10`; do echo $it; done; }; count_toten_by_two; count_toten_by_two_with_varname () { for each in `seq 0 2 10`; do echo $each; done; }; count_toten_by_two_with_varname; sayhelloifone () { local argument="${1}"; case "${argument}" in 1 ) echo hello;  ;; * ) echo didnt understand the argument.; sayhelloiftwo () { local argument="${1}"; case "${argument}" in 2 ) echo hello;  ;; * ) echo Dont understand the argument.;  ;; esac; }; sayhelloiftwo "$argument"; sayhelloiftwo "1"; sayhelloiftwo "2"; echo this is from inside of the loop.; echo yes // This is a comment.; for it in `seq 1 5`; do echo hello$it; done;  ;; esac; }; testing_let () { a=10; echo $a; }; testing_let; echo; sayhelloifone 2; echo; sayhelloifone 1; count_toten 1; echo; hello_world; }; myfunction () { local a="${1}"; for it in `seq 1 5`; do if [ $a = $it ]; then echo $a; elif [ 1 ]; then echo false $it; fi; done; }; 