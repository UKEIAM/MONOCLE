import { Divider } from "@mui/material"
import { useEffect, useState } from "react"
import { Comment as CommentType } from "gen/api"
import { useParams } from "react-router-dom"
import Comment from "./Comment"
import AddComment from "./AddComment"
import { useAuth } from "react-oidc-context"
import CommentsHeader from "./CommentsHeader"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

const CommentDivider = ({ enabled = true }: { enabled?: boolean }) => {
  return <>{enabled ? <Divider variant={"fullWidth"} /> : undefined}</>
}

export default function Comments() {
  const { CommentsApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const { patientId } = useParams()
  const auth = useAuth()
  const [comments, setComments] = useState<CommentType[]>([])
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<CommentType>()

  useEffect(() => {
    refreshComments()
  }, [])

  const refreshComments = () => {
    CommentsApi.getComments(patientId!)
      .then((response) => {
        setComments(
          response.data.sort(
            (a, b) =>
              // sort oldest to newest
              new Date(a.createdAt!).getTime() - new Date(b.createdAt!).getTime(),
          ),
        )
      })
      .catch(() => showErrorNotification("Fehler beim Laden der Kommentare"))
  }

  const handleToggleHighlight = (commentId: string) => {
    const comment = comments.find((e) => e.id === commentId)!
    CommentsApi.updateComment(commentId, { ...comment, highlighted: !comment.highlighted }).then(
      () => {
        showSuccessNotification("Kommentar erfolgreich geändert")
        refreshComments()
      },
    )
  }

  const handleDeleteComment = (commentId: string) => {
    if (commentId) {
      CommentsApi.deleteComment(commentId).then(() => {
        showSuccessNotification("Kommentar erfolgreich gelöscht")
        refreshComments()
        setConfirmOpen(false)
      })
    }
  }

  const deleteEntryConfirmation = (item: CommentType) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  const commentsHighlighted = comments.filter((e) => e.highlighted)

  return (
    <>
      <CommentsHeader />
      {commentsHighlighted.map((comment) => (
        <Comment
          comment={comment}
          highlight
          onToggleHighlight={handleToggleHighlight}
          onDelete={deleteEntryConfirmation}
        />
      ))}
      <CommentDivider enabled={commentsHighlighted.length !== 0} />
      {comments.map((comment) => (
        <Comment
          comment={comment}
          onToggleHighlight={handleToggleHighlight}
          onDelete={deleteEntryConfirmation}
        />
      ))}
      <AddComment onSubmit={refreshComments} author={auth.user?.profile.name} />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`Kommentar von ${itemToBeDeleted.author}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={() => handleDeleteComment(itemToBeDeleted.id!)}
        />
      )}
    </>
  )
}
