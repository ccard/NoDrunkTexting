#! /usr/bin/env ruby

require 'net/http'

http = Net::HTTP

path = "C:\\Users\\Ch\\Documents\\App Dev\\DrunkTexting\\src\\com\\google\\android\\mms\\pdu\\"

#"grepcode.com/file_/repository.grepcode.com/java/ext/com.google.android/android/4.4_r1/com/google/android/mms/pdu/PduBody.java/?v=source&disposition=attachment"
files = ["ContentType.java",
	"InvalidHeaderValueException.java",
	"MmsException.java",
	"pdu/AcknowledgeInd.java",
	"pdu/Base64.java",
	"pdu/CharacterSets.java",
	"pdu/DeliveryInd.java",
	"pdu/EncodedStringValue.java",
	"pdu/GenericPdu.java",
	"pdu/MultimediaMessagePdu.java",
	"pdu/NotifictionInd.java",
	"pdu/NotifyRespInd.java",
	"pdu/PduBody.java",
	"pdu/PduComposer.java",
	"pdu/PduContentTypes.java",
	"pdu/PduHeaders.java",
	"pdu/PduParser.java",
	"pdu/PduPart.java",
	"pdu/PduPersister.java",
	"pdu/QuotedPrintable.java",
	"pdu/ReadOrigInd.java",
	"pdu/RetrieveConfjava",
	"pdu/SendConf.java",
	"pdu/SendRequ.java",
	"util/AbstractCache.java",
	"util/DownloadDrmHelper.java",
	"util/DrmConvertSession.jav",
	"util/PduCache.java",
	"util/PduCacheEntry.java",
	"util/SqliteWrapper.java"]

file_paths = {}
files.each {|file| file_paths[file] = {:web => "/file_/repository.grepcode.com/java/ext/com.google.android/android/4.4_r1/com/google/android/mms/#{file}/?v=source&disposition=attachment", 
							:path => ""}}
files.each { |file| file_paths[file][:path] = "C:\\Users\\Ch\\Documents\\App Dev\\DrunkTexting\\src\\com\\google\\android\\mms\\#{file}"}
files.each { |file| file_paths[file][:path].gsub!(/\//,"\\")}

files.each do |file|
	puts "Writing file: #{file}"
	http = Net::HTTP.start("grepcode.com")
	resp = http.get file_paths[file][:web]
	open(file_paths[file][:path],'wb') do |f|
		f.write(resp.body)
	end
end
