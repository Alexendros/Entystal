param(
  [string]$Mode = "asset",
  [Parameter(Mandatory=$true)][string]$AssetId,
  [Parameter(Mandatory=$true)][string]$AssetDesc
)
$cmd = "core/run --mode $Mode --assetId $AssetId --assetDesc \"$AssetDesc\""
& sbt $cmd

