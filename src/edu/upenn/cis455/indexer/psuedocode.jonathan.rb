####################################
# doc_423049392093.bigfile
DOCUMENT_ID<deemegjonraj>URL<deemegjonraj>header1: some value<newline>header2: other value<deemegjonraj>BODY<deemegjonraj>
DOCUMENT_ID<deemegjonraj>URL<deemegjonraj>header1: some value<newline>header2: other value<deemegjonraj>BODY<deemegjonraj>

####################################
# url_423049392093.bigfile
DOCUMENT_ID(URL_HASH) DOCUMENT_ID(URL_HASH)
DOCUMENT_ID(URL_HASH) DOCUMENT_ID(URL_HASH)


# Creating the Forward Index from BigFile

class CreateForwardIndexFromBigFile

  def map
    params = LineDecoder.parse(line)
    doc_id = params[:doc_id]
    url = params[:url]
    headers = params[:header]
    document = params[:doc]

    Hittafy(document).each do |hit|
      emit()
    end
  end
end

