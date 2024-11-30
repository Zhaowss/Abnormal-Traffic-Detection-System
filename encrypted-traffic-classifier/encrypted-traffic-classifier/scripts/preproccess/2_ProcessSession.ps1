# Wei Wang (ww8137@mail.ustc.edu.cn)
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this file, You
# can obtain one at http://mozilla.org/MPL/2.0/.
# ==============================================================================

$SESSIONS_COUNT_LIMIT_MIN = 0
$SESSIONS_COUNT_LIMIT_MAX = 200
$TRIMED_FILE_LEN = 784
$SOURCE_SESSION_DIR = "2_Session\AllLayers"

echo "If Sessions more than $SESSIONS_COUNT_LIMIT_MAX we only select the largest $SESSIONS_COUNT_LIMIT_MAX."
echo "Finally Selected Sessions:"

$dirs = gci $SOURCE_SESSION_DIR -Directory
foreach($d in $dirs)
{
    $files = gci $d.FullName
    $count = $files.count
    if($count -gt $SESSIONS_COUNT_LIMIT_MIN)
    {             
        echo "$($d.Name) $count"        
        if($count -gt $SESSIONS_COUNT_LIMIT_MAX)
        {
            $files = $files | sort Length -Descending | select -First $SESSIONS_COUNT_LIMIT_MAX
            $count = $SESSIONS_COUNT_LIMIT_MAX
        }

        $files = $files | resolve-path
        $test  = $files | get-random -count ([int]($count/5))
        $train = $files | ?{$_ -notin $test}     

        $path_test  = "3_ProcessedSession\FilteredSession\Test\$($d.Name)"
        $path_train = "3_ProcessedSession\FilteredSession\Train\$($d.Name)"
        ni -Path $path_test -ItemType Directory -Force
        ni -Path $path_train -ItemType Directory -Force    

        cp $test -destination $path_test        
        cp $train -destination $path_train
    }
}