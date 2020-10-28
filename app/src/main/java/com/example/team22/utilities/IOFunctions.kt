package com.example.team22.utilities


import android.app.Application
import com.example.team22.location.KiteLocation
import java.io.*

//writes id of favourite KiteLocations to file '/favourites' in following format: 1,2,3 etc..
fun writeFavouriteIdsToFile(context: Application,
                            favourites: List<KiteLocation>?,
                            filename: String) {
    if(favourites != null){
        val favouritesFile = File(context.filesDir, filename)
        try {
            val fileOutputStream = FileOutputStream(favouritesFile)
            for ((i, kiteLocation) in favourites.withIndex()) {
                fileOutputStream.write(kiteLocation.id.toString().toByteArray())
                if (i != favourites.lastIndex) {
                    fileOutputStream.write(",".toByteArray())
                }
            }
            fileOutputStream.flush()
            fileOutputStream.close()
        }
        catch (e: FileNotFoundException) {
            println("Could not save to chosen file.")
        }
        catch (e : SecurityException){
            println("SECURITY EXCEPTION: Do not have permission to write to chosen file.")
        }

    }
}
//reads id of favourite KiteLocations from file '/favourites' and returns this as list of Ints.
fun readFavouriteIdsFromFile(context: Application, filename: String) : List<Int> {
    try {
        val favouritesFile = File(context.filesDir, filename)
        val inputStream = FileInputStream(favouritesFile)
        val stringBuilder = StringBuilder()
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        var receiveString = bufferedReader.readLine()
        while (receiveString != null) {
            stringBuilder.append(receiveString)
            receiveString = bufferedReader.readLine()
        }
        inputStream.close()
        val favouritesText = stringBuilder.toString()
        return if (favouritesText.isNotEmpty()) {
            favouritesText.split(",").map{it.toInt()}
        } else {
            emptyList()
        }

    }
    catch (e: FileNotFoundException) {
        return emptyList()
    }
    catch (e: IOException){
        return emptyList()
    }
}

