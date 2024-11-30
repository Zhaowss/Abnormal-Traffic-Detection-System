# Wei Wang (ww8137@mail.ustc.edu.cn)
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this file, You
# can obtain one at http://mozilla.org/MPL/2.0/.
# ==============================================================================

$TRIMED_FILE_LEN = 784

echo "All files will be trimed to $TRIMED_FILE_LEN length and if it's even shorter we'll fill the end with 0x00..."

$paths = @(('3_ProcessedSession\Anonymity\Train', '3_ProcessedSession\TrimedSession\Train'), ('3_ProcessedSession\Anonymity\Test', '3_ProcessedSession\TrimedSession\Test'))
foreach($p in $paths)
{
   foreach ($d in gci $p[0] -Directory)
   {
       ni -Path "$($p[1])\$($d.Name)" -ItemType Directory -Force
       foreach($f in gci $d.fullname)
       {
           $content = [System.IO.File]::ReadAllBytes($f.FullName)
           $len = $f.length - $TRIMED_FILE_LEN
           if($len -gt 0)
           {
               $content = $content[0..($TRIMED_FILE_LEN-1)]
           }
           elseif($len -lt 0)
           {
               $padding = [Byte[]] (,0x00 * ([math]::abs($len)))
               $content = $content += $padding
           }
           Set-Content -value $content -encoding byte -path "$($p[1])\$($d.Name)\$($f.Name)"
       }
   }
}